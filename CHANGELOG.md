## 1.2.0 (2020-05-05)

* Add support for ClojureScript on node.js (#87)

## 1.1.0 (2016-08-04)

* Replace `:project/foo` keywords with the value of `:foo` key in project map

## 1.0.3 (2016-05-06)

* Fix boot-environ to return updated fileset
* Print key when santizing value for easier debugging
* Set `*print-xxx*` vars to false when writing env-file

## 1.0.2 (2016-01-27)

* Cast non-string values to strings and output warning
* Fix boot-environ to work with pods

## 1.0.1 (2014-08-16)

* Use safer `clojure.edn/read-string` function to read environment files

## 1.0.0 (2014-08-16)

* First stable release
