(ns noir-test2.example.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css html5]]))

(defpartial layout [& content]
  (let [[title headline tagline announcement page] content]
	    (html5
	      [:head
	       [:meta {:charset "utf-8"}]
	       [:title title]
	       (include-css "/css/base.css")]
	      [:body {:id "foster"}
	       [:header
	        [:div
	         [:nav {"class" "navlist"}
	          [:ul 
	           [:li [:a {"href" "/signup" "class" "top-nav"} "Sign up!"]]]]]]
	       [:div {:id "wrap"}
	        [:div {:id "center-column"} 
	         [:section {:id "meta" :class "frame"}
	          [:section {:id "filariane"}
	           [:span "cohab"]]
	          [:span {:class "clear"}]]
	         [:div {:class "frame l" :id "contents"}
	          [:h1 headline]
	          [:p {:id "tagline"} tagline]
	          [:p {:class "announcement"} announcement]
	          [:p {:class "page"} page]]
	         [:footer [:div {:class "frame"} "Copyright &copy; 2012 | Services Etc."]]]]])))