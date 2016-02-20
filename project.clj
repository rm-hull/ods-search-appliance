(defproject rm-hull/wam "0.0.1-SNAPSHOT"`
  :description "ODs Search Appliance"
  :url "https://github.com/rm-hull/ods-search-appliance"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [org.clojure/data.json "0.2.6"]
    [org.clojure/data.csv "0.1.3"]
    [clj-http "2.1.0"]
    [compojure "1.4.0"]
    [ring "1.4.0"]
    [hiccup "1.0.5"]
    [table "0.5.0"]]
  :scm {:url "git@github.com:rm-hull/ods-search-appliance.git"}
  :plugins [
    [codox "0.9.1"]
    [lein-ring "0.9.7"]]
  :source-paths ["src"]
  :jar-exclusions [#"(?:^|/).git"]
  :uberjar-exclusions [#"\.SF" #"\.RSA" #"\.DSA"]
  :codox {
    :sources ["src"]
    :output-dir "doc/api"
    :src-dir-uri "http://github.com/rm-hull/ods-search-appliance/blob/master/"
    :src-linenum-anchor-prefix "L" }
  :min-lein-version "2.5.3"
  :profiles {
    :uberjar {:aot :all}
    :dev {
      :global-vars {*warn-on-reflection* true}
      :plugins [
        [lein-cloverage "1.0.6"]]}})
