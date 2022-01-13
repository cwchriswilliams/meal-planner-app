(ns cwchriswilliams.meal-planner-app.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [reagent-mui.material.css-baseline :refer [css-baseline]]
            [cwchriswilliams.meal-planner-app.events]
            [cwchriswilliams.meal-planner-app.subscriptions]
            [cwchriswilliams.meal-planner-app.views :as views]
            [cwchriswilliams.meal-planner-app.router :as router]
            [cwchriswilliams.meal-planner-app.route-view-mapping :as route-map]))




(defn mount-ui
  [ui]
  (rdom/render [css-baseline
                [ui]]
               (js/document.getElementById "app")))

(defn run
  []
  (route-map/register-routes)
  (router/start!)
  (rf/dispatch-sync [:initialize])
  (mount-ui views/ui))

(run)
