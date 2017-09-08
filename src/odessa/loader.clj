(ns odessa.loader
  (:require
   [clojure.java.io :as io]
   [clojure.data.csv :as csv]
   [clojure.string :as s]
   [taoensso.timbre :as timbre]
   [odessa.zip :as zip]))

(def data-sets {; Health authorities and support agencies
                :eauth "https://digital.nhs.uk/media/332/eauth/zip/eauth.zip"
                :espha "https://digital.nhs.uk/media/343/espha/zip/espha.zip"
                :ecsu "https://digital.nhs.uk/media/342/ecsu/zip/ecsu.zip"
                :ecsusite "https://digital.nhs.uk/media/341/ecsusite/zip/ecsusite.zip"
                :eother "https://digital.nhs.uk/media/340/eother/zip/eother.zip"
                :ensa "https://digital.nhs.uk/media/339/ensa/zip/ensa.zip"

  ; GP and GP practice related data
                :epraccur "https://digital.nhs.uk/media/372/epraccur/zip/epraccur.zip"
                :egpcur "https://digital.nhs.uk/media/370/egpcur/zip/egpcur.zip"
                :epracmem "https://digital.nhs.uk/media/379/epracmem/zip/epracmem.zip"
                :epcmem "https://digital.nhs.uk/media/378/epcmem/zip/epcmem.zip"
                :epracarc "https://digital.nhs.uk/media/376/epracarc/zip/epracarc.zip"
                :egparc "https://digital.nhs.uk/media/374/egparc/zip/egparc.zip"
                :ebranchs "https://digital.nhs.uk/media/393/ebranchs/zip/ebranchs.zip"
                :epharmacyhq "https://digital.nhs.uk/media/391/epharmacyhq/zip/epharmacyhq.zip"
                :edispensary "https://digital.nhs.uk/media/390/edispensary/zip/edispensary.zip"
                :enurse "https://digital.nhs.uk/media/388/enurse/zip/enurse.zip"
                :epcdp "https://digital.nhs.uk/media/387/epcdp/zip/epcdp.zip"
                :eabeydispgp "https://digital.nhs.uk/media/385/eabeydispgp/zip/eabeydispgp.zip"

  ; Other NHS organisations
                :eccg "https://digital.nhs.uk/media/354/eccg/zip/eccg1.zip"
                :eccgsite "https://digital.nhs.uk/media/353/eccgsite/zip/eccgsite1.zip"
                :etr "https://digital.nhs.uk/media/352/etr/zip/etr.zip"
                :ets "https://digital.nhs.uk/media/351/ets/zip/ets.zip"
                :etrust "https://digital.nhs.uk/media/350/etrust/zip/etrust.zip"
                :ect "https://digital.nhs.uk/media/349/ect/zip/ect"
                :ectsite "https://digital.nhs.uk/media/348/ectsite/zip/ectsite.zip"
                :ecare "https://digital.nhs.uk/media/347/ecare/zip/ecare.zip"
                :wlhb "https://digital.nhs.uk/media/346/wlhb/zip/wlhb.zip"
                :wlhbsite "https://digital.nhs.uk/media/345/wlhbsite/zip/wlhbsite.zip"
                :wlbs "https://digital.nhs.uk/media/344/whbs/zip/whbs.zip"

  ; Home countries
                :scotgp "https://digital.nhs.uk/media/582/scotgp/zip/scotgp.zip"
                :scotprac "https://digital.nhs.uk/media/586/scotprac/zip/scotprac.zip"
                :scotorg "https://digital.nhs.uk/media/585/scotorg/zip/scotorg.zip"
                :ngpcur "https://digital.nhs.uk/media/577/ngpcur/zip/ngpcur.zip"
                :ngpraccur "https://digital.nhs.uk/media/581/npraccur/zip/npraccur.zip"
                :niorg "https://digital.nhs.uk/media/579/niorg/zip/NIORG.zip"
                :eiom "https://digital.nhs.uk/media/576/eiom/zip/eiom.zip"

  ; Non-NHS organisations
                :ephp "https://digital.nhs.uk/media/414/ephp/zip/ephp.zip"
                :ephpsite "https://digital.nhs.uk/media/412/ephpsite/zip/ephpsite.zip"
                :enonnhs "https://digital.nhs.uk/media/411/enonnhs/zip/enonnhs.zip"
                :eschool "https://digital.nhs.uk/media/406/eschools/zip/eschools.zip"
                :lauth "https://digital.nhs.uk/media/403/Lauth/zip/Lauth.zip"
                :eprison "https://digital.nhs.uk/media/401/eprison/zip/eprison.zip"
                :ejustice "https://digital.nhs.uk/media/400/ejustice/zip/ejustice.zip"})

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
  (let [loader (fn [src] [src (-> src fetcher s/split-lines vec)])]
    (into {} (pmap loader sources))))
