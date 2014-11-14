;;; model

(model-type :uml)

;;; type representations

(representation :String String)
(representation :Integer Integer)

;;; uml-classes

(uml-class :City
           (key-uml-attribute :Name :String :1 :1))

(uml-class :Person
           (key-uml-attribute :Name :String :1 :1)
           (key-uml-attribute :Surname :String :1 :1)
           (uml-attribute :Age :Integer :0 :1))

(uml-class :Car
           (key-uml-attribute :ID :Integer :1 :1)
           (uml-attribute :Accessories :String :0 :1)
           (uml-attribute :Accessory :String :0 :*))

;;; associations

(association
  :Citizen
  "Is citizen"
  (role :Citizen :Person :0 :*)
  (role :City :City :0 :*))

(association
  :Owner
  "Owns a car"
  (role :Owner :Person :0 :*)
  (role :Car :Car :1 :1))
