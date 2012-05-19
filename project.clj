(defproject noir-test2 "1.0.0-SNAPSHOT"
  :description "Enhancements to noir.util.test"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [htmlcleaner "2.2.4"]
                 [ring-mock "0.1.2"]
                 [peridot "0.0.5"]
                 [noir "1.3.0-beta3"]]
  :dev-dependencies [[lein-eclipse "1.0.0"]]
  :git-dependencies [["https://github.com/nickbauman/htmlcleaner.git"]]
  :extra-classpath-dirs [".lein-git-deps/htmlcleaner/target/classes"])
