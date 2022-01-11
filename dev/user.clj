(ns user
  (:require [shadow.cljs.devtools.api :as shadow]
            [shadow.cljs.devtools.server :as server]))

(defn cljs-repl
  "Connects to a given build-id. Defaults to `:app`."
  ([]
   (cljs-repl :app))
  ([build-id]
   (server/start!)
   (shadow/watch build-id)
   (shadow/nrepl-select build-id)))

;;;;;;;;;;; Example dir-locals.el ;;;;;;;;;;;;;
;;
;; ((nil . ((cider-clojure-cli-global-options . "-A:dev")
;;          (cider-preferred-build-tool       . clojure-cli)
;;          (cider-default-cljs-repl          . custom)
;;          (cider-custom-cljs-repl-init-form . "(do (user/cljs-repl))")
;;          (eval . (progn
;;                   (make-variable-buffer-local 'cider-jack-in-nrepl-middlewares)
;;                   (add-to-list 'cider-jack-in-nrepl-middlewares "shadow.cljs.devtools.server.nrepl/middleware"))))))
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
