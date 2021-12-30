(ns cwchriswilliams.meal-planner-app.routes
  (:require [re-frame.core :as rf]
            [pushy.core :as pushy]
            [bidi.bidi :as bidi]))

(defmulti panel :active-panel)

(defmethod panel :default [_] [:div "404 - Panel not found"])

(def routes 
  [
      "/" {"" :meal-items-list-panel
           "meal-items" :meal-items-list-panel
           ["meal-item/" :id] :meal-item-panel
           ["meal-item/edit-name/" :id] :edit-meal-item-name-panel
           ["meal-item/add-step/" :id] :add-step-panel
           ["meal-item/edit-step/" :id "/" :step-id] :edit-step-panel}
  ])

(defn parse
  [url]
  (bidi/match-route routes url))

(defn dispatch
  [route]
  (let [panel (keyword (str (name (:handler route))))]
    (rf/dispatch [:set-active-panel-details {:active-panel panel :details (:route-params route)}])))

(defonce history (pushy/pushy dispatch parse))

(defn url-for
  [& panel-to-naviate-to]
  (apply bidi/path-for (into [routes] panel-to-naviate-to)))

(defn navigate!
  ([panel-to-naviate-to]
   (pushy/set-token! history (url-for panel-to-naviate-to)))
  ([panel-to-naviate-to id]
   (pushy/set-token! history (url-for panel-to-naviate-to :id id)))
  ([panel-to-naviate-to id step-id]
   (pushy/set-token! history (url-for panel-to-naviate-to :id id :step-id step-id))))

(defn start!
  []
  (pushy/start! history))

(rf/reg-fx
 :navigate
 (fn [[panel-to-navigate-to]]
   (navigate! panel-to-navigate-to)))

(rf/reg-fx
 :navigate-to-element-by-id
 (fn [[panel-to-navigate-to id]]
   (navigate! panel-to-navigate-to id)))

(rf/reg-fx
 :navigate-to-edit-step-panel
 (fn [[panel-to-navigate-to id step-id]]
   (navigate! panel-to-navigate-to id step-id)))