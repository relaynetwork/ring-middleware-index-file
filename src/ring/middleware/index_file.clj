(ns ring.middleware.index-file
  (:require
   ring.util.response))

(defn- redirect-uri [request path-with-slash]
  (if (:query-string request)
    (str path-with-slash "?" (:query-string request))
    path-with-slash))

(defn wrap-index-path [handler path & opts]
  (let [path-with-slash (str path "/")
        opts            (merge {:root  "resources/public"
                                :index "index.html"}
                               (apply hash-map opts))]
    (fn [request]
      (let [uri             (:uri request)
            index-path      (str (:root opts) path-with-slash (:index opts))]
        (cond
          ;; if the uri does not end in a slash, redirect so it does...
          (= path uri)
          (ring.util.response/redirect (redirect-uri request path-with-slash))

          (= path-with-slash uri)
          (ring.util.response/file-response index-path)

          :else
          (handler request))))))