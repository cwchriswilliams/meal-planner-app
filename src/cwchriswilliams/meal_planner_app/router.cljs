(ns cwchriswilliams.meal-planner-app.router
  (:require [re-frame.core :as rf]
            [pushy.core :as pushy]
            [cwchriswilliams.meal-planner-app.routes-management :as routes]))

(defn dispatch
  [route]
  (let [panel (keyword (str (name (:handler route))))]
    (rf/dispatch [:set-active-panel-details {:active-panel panel :details (:route-params route)}])))

(defonce history (pushy/pushy dispatch routes/parse))


(defn navigate!
  ([panel-to-naviate-to]
   (pushy/set-token! history (routes/url-for panel-to-naviate-to)))
  ([panel-to-naviate-to id]
   (pushy/set-token! history (routes/url-for panel-to-naviate-to :id id)))
  ([panel-to-naviate-to id step-id]
   (pushy/set-token! history (routes/url-for panel-to-naviate-to :id id :step-id step-id))))

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
