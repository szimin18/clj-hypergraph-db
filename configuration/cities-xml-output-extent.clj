
(function
  :trim-all
  [v]
  (map
    (fn
      [s]
      (let [s (atom s)]
        (while (#{\space} (first @s))
          (swap! s rest))
        (while (#{\space} (last @s))
          (swap! s drop-last))
        (apply str @s)))
    v))

(function
  :regex-split
  [s pattern]
  (if s
    (clojure.string/split s pattern)
    []))

(function
  :join
  [coll sep]
  (if (and coll (coll? coll) (not-empty coll))
    (clojure.string/join sep coll)))

(function
  :concat
  [& seqs]
  (for [s seqs
        e s]
    e))


(function
  :sum
  [& args]
  (apply + (map #(Integer. %) args)))


(function
  :divide
  [x y]
  (if (> (Integer. y) 0)
    (/ (Integer. x) (Integer. y))
    0))


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
                                                        (call :regex-split :Accessories #";")
                                                        (aggregate :Accessory)))
                                            :accessoriesList)
                                      (mapping (call :join :accessoriesList ";") [:Accessories :FullList :FullList-text-node])
                                      (mapping-each :accessoriesList [:Accessories :Accessory :Accessory-text-node]))))
