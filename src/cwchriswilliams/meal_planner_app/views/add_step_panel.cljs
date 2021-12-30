(ns cwchriswilliams.meal-planner-app.views.add-step-panel
  (:require [cwchriswilliams.meal-planner-app.views.edit-step-panel :refer [edit-step-panel]]
            [cwchriswilliams.meal-planner-app.routes :as routes]))

(defmethod routes/panel :add-step-panel [details]
  #(edit-step-panel (:details details)))