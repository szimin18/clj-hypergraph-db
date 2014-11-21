
(function
  :trim-all
  [v]
  (map
    (fn
      [s]
      (let [s (atom s)]
        (while (#{\space} (first s))
          (swap! s rest))
        (while (#{\space} (last s))
          (swap! s drop-last))
        @s))
    v))

(function
  :regex-split
  [s delim]
  (clojure.string/split s delim))

(function
  :join
  [coll sep]
  (clojure.string/join sep coll))

(function
  :concat
  [& seqs]
  (apply concat seqs))


(function
  :sum
  [& args]
  (apply + args))


(for-each :City
          (add-token [:Earth :City]
                     (bind 0 :sumAge)
                     (bind 0 :citizenCount)
                     (bind 0 :carCount)
                     (for-each :Person
                               (associated-with-for [:..] :City :Citizen :Citizen
                                                    (bind (call :sum :sumAge :Age) :sumAge)
                                                    (bind (call :sum :citizenCount 1) :citizenCount)
                                                    (for-each :Car
                                                              (associated-with-for [:..] :Owner :Owner :Car
                                                                                   (bind (call :sum :carCount 1) :carCount)))))
                     (mapping :Name [:Name :Name-text-node])
                     (mapping :carCount [:CarsCount :CarsCount-text-node])
                     (mapping (call :divide :sumAge :citizenCount) [:AverageAge :AverageAge-text-node])))

(for-each :Person
          (associated-with [:Earth :City] :City :Citizen :Citizen
                           (add-token [:Citizens :Person]
                                      (mapping :Name [:Name :Name-text-node])
                                      (mapping :Surname [:Surname :Surname-text-node])
                                      (mapping :Age [:Age :Age-text-node]))))

(for-each :Car
          (associated-with [:Earth :City :Citizens :Person] :Owner :Owner :Car
                           (add-token [:Cars :Car]
                                      (mapping :ID [:ID :ID-text-node])
                                      (bind (call :trim-all
                                                  (call :concat
                                                        (call :regex-split :Accessories ";")
                                                        (aggregate :Accessory)))
                                            :accessoriesList)
                                      (mapping (call :join :accessoriesList "") [:FullList :FullList-text-node])
                                      (mapping-each :accessoriesList [:Accessories :Accessory :Accessory-text-node]))))
