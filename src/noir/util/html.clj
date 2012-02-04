(ns noir.util.html
  ^{:doc "A Clojure wrapper around a fork of the Java HtmlCleaner library."}
  (:require [clojure.zip :as zip]
            [clojure.xml :as xml])
  (:use clojure.contrib.zip-filter.xml) 
  (:import [org.htmlcleaner 
            HtmlCleaner
            CleanerProperties
            PrettyXmlSerializer
            TagNode]
           [java.io StringWriter]))

(defn cleaner-props
  "Create a new CleanerProperties object with some sane defaults"
  []
  (doto (CleanerProperties.)
    (.setOmitXmlDeclaration true)
    (.setTranslateSpecialEntities true)
    (.setTransResCharsToNCR true)
    (.setOmitComments true)))
    
(defn cleaner
  "Create a new HtmlCleaner instance from a CleanerProperties instance"
  [^CleanerProperties jcleaner-props]
  (HtmlCleaner. jcleaner-props))

(defn html2dom 
  "Converts an HTML object into a DOM object of HTMLCleaner's TagNode type"
  [^String html-string ^CleanerProperties jcleaner-props]
  (if html-string
    (.clean (cleaner jcleaner-props) html-string)
    (throw (RuntimeException. "html-string is null" ))))

(defn to-markup
  "Converts an HTMLCleaner TagNode object into an XML document"
  [^org.htmlcleaner.TagNode tag-node ^CleanerProperties jcleaner-props]
  (let [writer (java.io.StringWriter. 32)]
    (.write (PrettyXmlSerializer. jcleaner-props) tag-node writer "UTF-8" false) 
    (str writer)))
 
(defn html2xml
  "Converts an html document into a valid XML document, balancing tags and cleaning up attributes if possible."
  [^String html-string]
  (let [jcleaner-props (cleaner-props)
        jtagnode (.clean cleaner html-string)]
    (to-markup jtagnode jcleaner-props)))

(defn zip-xml [xml-string]
  "Converts XML into a clojure datastructure"
  (zip/xml-zip (xml/parse (java.io.ByteArrayInputStream. (.getBytes xml-string)))))

(defn find-elem-by-name
  [html-string element-name]
  (let [jcleaner-props (cleaner-props)
        jtagnode (.findElementByName (html2dom html-string jcleaner-props) element-name true)]
    (if jtagnode 
      (to-markup jtagnode jcleaner-props))))

(defn find-elems-of-attr
  "Finds elements having an attribute"
  [html-string attr-name]
  (if (and html-string attr-name)
    (let [jcleaner-props (cleaner-props)
          jtagnodes (.getElementsHavingAttribute (html2dom html-string jcleaner-props) attr-name true)]
      (if (seq jtagnodes)
        (map #(to-markup % jcleaner-props) jtagnodes)))
    (throw (RuntimeException. (str "missing parameters: " 
                                   (if html-string 
                                     "" 
                                     "html-string is nil ") 
                                   (if attr-name
                                     ""
                                     "attr-name is nil "))))))

(defn find-elem-of-attr-val
  "Finds elements matching an attribute name and value."
  [html-string attr-name attr-val]
  (let [jcleaner-props (cleaner-props)
        jtagnode (.findElementByAttValue (html2dom html-string jcleaner-props) attr-name attr-val true false)]
    (if jtagnode 
      (to-markup jtagnode jcleaner-props))))

(defn find-elem-with-matching-attrs
  "Finds elements matching ALL attribute names, values and element text (if present): 
  'html-string' '<html><head>...' 
  'hattr-maps' {:foo 'bar' :baz 'quux'} 
  'tag-value' can be nil, otherwise whatever you wish to assert between the open and close of the element"
  [html-string hattr-maps tag-value]
  (if html-string
    (let [attr-maps (into {} (map (fn[[x y]] [(name x) y]) hattr-maps))
          jcleaner-props (cleaner-props)
          cnv-vec-fn (fn[pair] (into (vector) pair))
          dom (html2dom html-string jcleaner-props)
          jTagNode (if tag-value
                     (.findElementWithValueAttNamesAndValues dom tag-value (keys attr-maps) (vals attr-maps) true false)
                     (.findElementWithAttNamesAndValues dom (keys attr-maps) (vals attr-maps) true))]
      (if-let [jtn jTagNode]
        (to-markup jtn jcleaner-props)))
    (throw (RuntimeException. (str "html-string is " html-string)))))

(defn eval-xp-expr
  "Evaluates an xpath expression on the html-string. The XPath implementation in HtmlCleaner is NOT complete. YMMV."
  [html-string xpath-expr]
  (seq (.evaluateXPath (html2dom html-string) xpath-expr)))