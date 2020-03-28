(ns user
  (:require [figwheel.main.api :as fig-api]))

(defn start-build []
  (fig-api/start {:mode :serve} "dev")
  (fig-api/cljs-repl "dev"))
