(database :sql
          (default-credentials "salon24db" "sna_user" "pass"))

(table "posts" :posts
       (column "Id" :Id :pk :notnull)
       (column "author_id" :author)
  (column "category" :category))
