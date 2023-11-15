(defproject rm-hull/ods-search-appliance "0.1.1"
  :description "ODS Search Appliance ('Odessa')"
  :url "https://github.com/rm-hull/ods-search-appliance"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.11.1"]
    [org.clojure/data.json "2.4.0"]
    [org.clojure/data.csv "1.0.1"]
    [com.taoensso/timbre "6.3.1"]
    [rm-hull/jasentaa "0.2.5"]
    [compojure "1.7.0"]
    [ring "1.10.0"]
    [ring-logger-timbre "0.7.6"]
    [metrics-clojure-ring "2.10.0"]]
  :scm {:url "git@github.com:rm-hull/ods-search-appliance.git"}
  :ring {
    :handler odessa.handler/app }
  :source-paths ["src"]
  :jar-exclusions [#"(?:^|/).git"]
  :uberjar-exclusions [#"\.SF" #"\.RSA" #"\.DSA"]
  :codox {
    :source-paths ["src"]
    :output-path "doc/api"
    :source-uri "http://github.com/rm-hull/ods-search-appliance/blob/master/{filepath}#L{line}" }
  :min-lein-version "2.8.1"
  :profiles {
    :uberjar {:aot :all}
    :dev {
      :global-vars {*warn-on-reflection* true}
      :plugins [
        [lein-cloverage "1.2.4"]
        [lein-codox "0.10.8"]
        [lein-cljfmt "0.9.2"]
        [lein-ring "0.12.6"]]}})
