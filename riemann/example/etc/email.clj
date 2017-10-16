(ns example.etc.email
  (:require [riemann.email :refer :all]))

(def email (mailer { :host "mailserver"
                     :from "riemann@example.com"}))
