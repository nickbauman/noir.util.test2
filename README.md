# noir-test2

If you want to write more comprehensive integration tests in the noir framework with the following idiom:

        (deftest test-signup-validation
                (-> (send-request [:post "/signup/"] {"email" "foo.bar@gmail.com" 
                                      "email-confirm" "foo.bar@kkd"
                                      "password" "f00lsG4m3" 
                                      "password-confirm" "f00lsllllll"})
                        (has-status 200)
                        (!body-contains #"Thanks for Signing Up")
                        (has-tags [[:form {:action "/signup"}]
                                [:p {:class "error"} "Passwords do not match"]
                                [:p {:class "error"} "Emails do not match"]])))

Note this uses a 3rd party HTML parser to aid in the assertions.

## Usage

To use this, add the *lein-git-deps* dependency to your dev-dependencies property in your project.clj. Then add git repo for the HTML parsing library the tests use.

        ...
        :dev-dependencies [lein-git-deps "0.0.1-SNAPSHOT"]
        ...
        :git-dependencies [["https://github.com/nickbauman/htmlcleaner.git"]]
        :extra-classpath-dirs [".lein-git-deps/htmlcleaner/target/classes"]

Then, run:

        lein deps
        lein git-deps

## License

Copyright (C) 2012 Nick Bauman, Chris Granger

Distributed under the Eclipse Public License, the same as Clojure.

