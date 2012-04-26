(ns ring.middleware.index-file
  (:require
   ring.util.response))

(defn- redirect-uri [request path-with-slash]
  (if (:query-string request)
    (str path-with-slash "?" (:query-string request))
    path-with-slash))

(defn wrap-index-paths [handler doc-root]
  (fn [request]
    (let [get-method? (= :get (:request-method request))
          uri         (:uri request)
          fpath       (str doc-root uri "/index.html")
          exists?     (.exists (java.io.File. fpath))]
      (cond
        (and exists? (not (.endsWith uri "/")))
        (ring.util.response/redirect (redirect-uri request (str uri "/")))

        exists?
        (ring.util.response/file-response fpath)

        :no-index-file-found
        (handler request)))))
