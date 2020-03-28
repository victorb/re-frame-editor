(ns ^:figwheel-hooks re-frame-editor.core
  (:require
    [cljs.pprint :refer [pprint]]
    [goog.dom :as gdom]
    [reagent.core :as r]
    [re-frame.core :as rf]))

(println "This text is printed from src/re_frame_editor/core.cljs. Go ahead and edit it and see reloading in action.")

(defn multiply [a b] (* a b))

(defn initial-state []
  {::text "Hello There"
   ::user {::name "Victor"
           ::age 26}})

(rf/reg-event-db
  ::initialize
  (fn [] (initial-state)))

(rf/reg-sub
  ::text
  (fn [db]
    (::text db)))

(rf/reg-sub
  ::greeting
  (fn [db]
    (str (::text db) " " (-> db ::user ::name))))

(rf/reg-event-db
  ::set-text
  (fn [db [_ new-text]]
    (assoc db ::text new-text)))

(defn get-app-element []
  (gdom/getElement "app"))

(rf/reg-sub
  ::db
  (fn [db] db))

(rf/reg-event-db
  ::set-db-value
  (fn [db [_ k v]]
    (assoc-in db k v)))

(defn $single-value [k v]
  [:div
   (let [c (count k)
         marginize? (> c 1)
         margin-val 10]
     {:style {:margin-left (when marginize? (* c margin-val))}})
   [:pre
    (with-out-str (pprint k))]
   [:input
    {:type :text
     :onChange #(rf/dispatch [::set-db-value k (-> % .-target .-value)])
     :value v}]])

(defn conjv [a b]
  (into (vector) (conj a b)))

(defn $state-value
  ([m prev-keys]
   (if (map? m)
     [:div
      (map (fn [v]
             [$state-value v (conjv prev-keys (first v))])
           m)]
     (let [k (first m)
           v (second m)]
       (if (map? (second m))
         [$state-value v prev-keys]
         [$single-value prev-keys v])))))


(defn $state-editor []
  [:div
    (let [state @(rf/subscribe [::db])]
      [$state-value state []])])

(defn $entire-state []
  [:pre (with-out-str (pprint @re-frame.db/app-db))])

(defn hello-world []
  [:div
   [:h1 @(rf/subscribe [::greeting])]
   [:input
    {:type :text
     :value @(rf/subscribe [::text])
     :onChange #(rf/dispatch [::set-text (-> % .-target .-value)])}]
   [:h2 "Nested Value example"]
   [:h2 "State Editor"]
   [$state-editor]
   [:h2 "Entire State"]
   [$entire-state]])

(defn mount [el]
  (r/render-component [hello-world] el))

(defn mount-app-element []
  (rf/dispatch [::initialize])
  (when-let [el (get-app-element)]
    (mount el)))

(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element))
