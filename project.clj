(defproject rm-hull/ods-search-appliance "0.0.1-SNAPSHOT"`
  :description "ODS Search Appliance ('Odessa')"
  :url "https://github.com/rm-hull/ods-search-appliance"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [org.clojure/data.json "0.2.6"]
    [org.clojure/data.csv "0.1.3"]
    [com.taoensso/timbre "4.4.0"]
    [rm-hull/jasentaa "0.2.3"]
    [compojure "1.5.0"]
    [ring "1.5.0"]
    [hiccup "1.0.5"]
    [ring-logger-timbre "0.7.5"]
    [metrics-clojure-ring "2.7.0"]]
  :scm {:url "git@github.com:rm-hull/ods-search-appliance.git"}
  :ring {
    :handler odessa.handler/app }
  :plugins [
    [lein-ring "0.9.7"]]
  :source-paths ["src"]
  :jar-exclusions [#"(?:^|/).git"]
  :uberjar-exclusions [#"\.SF" #"\.RSA" #"\.DSA"]
  :codox {
    :source-paths ["src"]
    :output-path "doc/api"
    :source-uri "http://github.com/rm-hull/ods-search-appliance/blob/master/{filepath}#L{line}" }
  :min-lein-version "2.6.1"
  :profiles {
    :uberjar {:aot :all}
    :dev {
      :global-vars {*warn-on-reflection* true}
      :plugins [
        [lein-cloverage "1.0.6"]
        [lein-codox "0.9.5"]]}})
