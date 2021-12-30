(ns cwchriswilliams.meal-planner-app.views
  (:require [re-frame.core :as rf]
            [cwchriswilliams.meal-planner-app.routes :as routes]
            [cwchriswilliams.meal-planner-app.views.edit-meal-item-name]
            [cwchriswilliams.meal-planner-app.views.add-step-panel]
            [cwchriswilliams.meal-planner-app.views.edit-step-panel]
            [cwchriswilliams.meal-planner-app.views.meal-item-list]
            [cwchriswilliams.meal-planner-app.views.meal-item-details]))

(defn main-panel
  []
  (let [active-panel @(rf/subscribe [:active-panel-details])]
    [(routes/panel active-panel)]))

(defn ui
  []
  [main-panel])