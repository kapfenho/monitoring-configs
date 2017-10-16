(ns example.etc.graphite
  (:require [riemann.graphite :refer :all]
            [riemann.config :refer :all]))

(defn add-environment-to-graphite [event] (str "my.hosts.", 
                        (riemann.graphite/graphite-path-percentiles event)))

(def graph (async-queue! :graphite {:queue-size 1000}
           (graphite {:host "graphite" :path add-environment-to-graphite})))
