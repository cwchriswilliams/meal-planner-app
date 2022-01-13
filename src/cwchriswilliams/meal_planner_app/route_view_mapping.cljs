(ns cwchriswilliams.meal-planner-app.route-view-mapping
  (:require [cwchriswilliams.meal-planner-app.routes-management :as route-m]
            [cwchriswilliams.meal-planner-app.views.meal-item-list :as meal-item-list-view]
            [cwchriswilliams.meal-planner-app.views.meal-item-details :as meal-item-details-view]
            [cwchriswilliams.meal-planner-app.views.edit-meal-item-name :as edit-meal-item-name-view]
            [cwchriswilliams.meal-planner-app.views.edit-step-panel :as edit-step-view]
            [cwchriswilliams.meal-planner-app.views.not-found-panel :as not-found-view]))

(defn register-routes
  []
  (route-m/register-route "" :meal-items-list-panel meal-item-list-view/meal-item-list)
  (route-m/register-route "meal-item/add-item" :add-meal-item-panel edit-meal-item-name-view/edit-meal-item-name-panel)
  (route-m/register-route ["meal-item-view/" :id] :meal-item-panel meal-item-details-view/meal-item-panel)
  (route-m/register-route ["meal-item/edit-name/" :id] :edit-meal-item-name-panel edit-meal-item-name-view/edit-meal-item-name-panel)
  (route-m/register-route ["meal-item/add-step/" :id] :add-step-panel edit-step-view/edit-step-panel)
  (route-m/register-route ["meal-item/edit-step/" :id "/" :step-id] :edit-step-panel edit-step-view/edit-step-panel)
  (route-m/register-route true :not-found  not-found-view/not-found-panel))
