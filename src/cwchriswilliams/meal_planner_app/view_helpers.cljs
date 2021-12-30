(ns cwchriswilliams.meal-planner-app.view-helpers)

(defn event-value
  [e]
  (.. e -target -value))
