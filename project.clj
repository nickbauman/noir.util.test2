(defproject noir-test2 "1.0.0-SNAPSHOT"
  :description "Enhancements to noir.util.test"
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [noir "1.2.1"]]
  :dev-dependencies [[lein-git-deps "0.0.1-SNAPSHOT"]
                     [lein-eclipse "1.0.0"]]
  :git-dependencies [["https://github.com/nickbauman/htmlcleaner.git"]]
  :extra-classpath-dirs [".lein-git-deps/htmlcleaner/target/classes"])
