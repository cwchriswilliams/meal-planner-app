(ns cwchriswilliams.meal-planner-app.views
  (:require [re-frame.core :as rf]
            [cwchriswilliams.meal-planner-app.routes-management :as routes]))

(defn main-panel
  []
  (let [active-panel @(rf/subscribe [:active-panel-details])
        handler (routes/handler-for (:active-panel active-panel))]
    [handler (:details active-panel)]))

(defn ui
  []
  [main-panel])
