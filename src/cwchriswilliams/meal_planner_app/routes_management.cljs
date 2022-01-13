(ns cwchriswilliams.meal-planner-app.routes-management
  (:require [bidi.bidi :as bidi]))

(def -view-map-atom (atom {}))

(defn register-view-for-view-map [views-atom panel-id f]
  (swap! views-atom #(assoc % panel-id f)))

(def -routes-atom (atom ["/" {}]))

(defn parse-for-routes
  [routes url]
  (when (empty? routes) (throw (js/Error. "Routes not defined")))
  (bidi/match-route routes url))


(defn parse [url] (parse-for-routes @-routes-atom url))

(defn url-for-routes
  [routes & panel-to-naviate-to]
  (apply bidi/path-for (into [routes] panel-to-naviate-to)))

(defn url-for
  [& panel-to-navigate-to]
  (apply (partial url-for-routes @-routes-atom) panel-to-navigate-to))

(defn register-route-for-routes-and-view-map
  [routes-atom view-map-atom path handler handler-fn]
  (swap! routes-atom assoc-in [1 path] handler)
  (register-view-for-view-map view-map-atom handler handler-fn))

(def register-route (partial
                     register-route-for-routes-and-view-map
                     -routes-atom
                     -view-map-atom))

(defn get-handler-for [panel-id]
  (get @-view-map-atom panel-id))
