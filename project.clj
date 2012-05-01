(defproject noir-test2 "1.0.0-SNAPSHOT"
  :description "Enhancements to noir.util.test"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.3.0-beta3"]]
  :dev-dependencies [[lein-git-deps "0.0.1-SNAPSHOT"]
                     [lein-eclipse "1.0.0"]]
  :git-dependencies [["https://github.com/nickbauman/htmlcleaner.git"]]
  :extra-classpath-dirs [".lein-git-deps/htmlcleaner/target/classes"])
