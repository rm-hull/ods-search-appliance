(ns odessa.loader
  (:require
   [clojure.java.io :as io]
   [clojure.data.csv :as csv]
   [clojure.string :as s]
   [taoensso.timbre :as timbre]
   [odessa.zip :as zip]))

(defn data-sets [abbr]
  (case abbr
    :epraccur (str "https://files.digital.nhs.uk/assets/ods/current/" (name abbr) ".zip")
    (str "https://nhsenglandfilestore.s3.amazonaws.com/ods/" (name abbr) ".zip")))

(def field-names [:organisation-code
                  :name
                  :national-grouping
                  :high-level-health-geography
                  :address-line-1
                  :address-line-2
                  :address-line-3
                  :address-line-4
                  :address-line-5
                  :postcode
                  :open-date
                  :close-date
                  :status-code
                  :organisation-sub-type-code
                  :organisation-type-code
                  :join-provider-date
                  :left-provider-date
                  :contact-telephone-number
                  :contact-name
                  :unused-20
                  :organisation-type-code
                  :amended-record-indicator
                  :wave-number
                  :gor-code
                  :unused-25
                  :prescribing-setting
                  :country-code])

(def indexable-field-offsets [0 1 2 3 4 5 6 7 8 9 17 18])

(defn number-to-bool [s]
  (= s "0"))

(defn yyyymmdd-to-iso-8601 [dt]
  (if (or (empty? dt) (not= (count dt) 8))
    nil
    (str (subs dt 0 4) "-" (subs dt 4 6) "-" (subs dt 6 8))))

(def formatters {:open-date yyyymmdd-to-iso-8601
                 :close-date yyyymmdd-to-iso-8601
                 :join-provider-date yyyymmdd-to-iso-8601
                 :left-provider-date yyyymmdd-to-iso-8601
                 :amended-record-indicator number-to-bool})

(defn ^:private format-value [[k v]]
  [k ((get formatters k identity) v)])

(defn to-map [^String csv-record]
  (->>
   (csv/read-csv csv-record)
   first
   (zipmap field-names)
   (remove (fn [[k v]] (empty? v)))
   (map format-value)
   (into (sorted-map))))

(defn extract-indexable-fields [csv-record]
  (->>
   indexable-field-offsets
   (map (first (csv/read-csv csv-record)))
   (remove empty?)))

(defn extract-primary-key [csv-record]
  (ffirst (csv/read-csv csv-record)))

(defn file-fetcher [source]
  (let [basename (name source)
        csv (str basename ".csv")]
    (slurp (str "data/ods/" csv))))

(defn http-fetcher [source]
  (let [basename (name source)
        zip (str basename ".zip")
        csv (str basename ".csv")
        url (data-sets source)]
    (try
      (if-let [entry (zip/extract url #(= (:filename %) csv))]
        (apply str (map char (:bytes entry))))
      (catch Exception ex
        (throw
         (ex-info (str "Failed to retrieve: " url) {:cause ex}))))))

(defn load-data [fetcher sources]
  (let [loader (fn [src]
                 (try
                   [src (-> src fetcher s/split-lines vec)]
                   (catch Exception ex
                     (throw
                      (ex-info (str "Failed to process: " src) {:cause ex})))))]
    (into {} (pmap loader sources))))
