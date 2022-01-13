(ns cwchriswilliams.meal-planner-app.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [reagent-mui.material.css-baseline :refer [css-baseline]]
            [cwchriswilliams.meal-planner-app.events]
            [cwchriswilliams.meal-planner-app.subscriptions]
            [cwchriswilliams.meal-planner-app.views :as views]
            [cwchriswilliams.meal-planner-app.routes :as routes]
            [cwchriswilliams.meal-planner-app.route-view-mapping :as route-mapper]))




(defn mount-ui
  [ui]
  (rdom/render [css-baseline
                [ui]]
               (js/document.getElementById "app")))

(defn run
  []
  (route-mapper/register-routes)
  (routes/start!)
  (rf/dispatch-sync [:initialize])
  (mount-ui views/ui))

(run)
