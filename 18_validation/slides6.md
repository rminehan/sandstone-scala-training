---
author: Rohan
date: 2022-10-31
title: Play Demo Continued Continued
---

```
 ____  _
|  _ \| | __ _ _   _
| |_) | |/ _` | | | |
|  __/| | (_| | |_| |
|_|   |_|\__,_|\__, |
               |___/
 ____
|  _ \  ___ _ __ ___   ___
| | | |/ _ \ '_ ` _ \ / _ \
| |_| |  __/ | | | | | (_) |
|____/ \___|_| |_| |_|\___/

  ____            _   _                      _
 / ___|___  _ __ | |_(_)_ __  _   _  ___  __| |
| |   / _ \| '_ \| __| | '_ \| | | |/ _ \/ _` |
| |__| (_) | | | | |_| | | | | |_| |  __/ (_| |
 \____\___/|_| |_|\__|_|_| |_|\__,_|\___|\__,_|

  ____            _   _                      _
 / ___|___  _ __ | |_(_)_ __  _   _  ___  __| |
| |   / _ \| '_ \| __| | '_ \| | | |/ _ \/ _` |
| |__| (_) | | | | |_| | | | | |_| |  __/ (_| |
 \____\___/|_| |_|\__|_|_| |_|\__,_|\___|\__,_|
```

`playDemo.map(...).map(...)`

---

# Today

Carry on last week's demo

---

# Recap

```json
POST /user
{
  "name": "Boban Jones",
  "age": 26
}
```

---

# The point

Think about modelling

Getting it right makes life easier later

---

# Single Responsibility Models

Easier to keep your models strong and focused when they do one thing

---

# Tooling

If your tools are giving you trouble,

avoid the temptation to weaken your models to make your tools happy

---

# Agenda

- database layer


- implement `GET`


- chimney!

---

# To the demo!

---

```
 ____
/ ___| _   _ _ __ ___  _ __ ___   __ _ _ __ _   _
\___ \| | | | '_ ` _ \| '_ ` _ \ / _` | '__| | | |
 ___) | |_| | | | | | | | | | | | (_| | |  | |_| |
|____/ \__,_|_| |_| |_|_| |_| |_|\__,_|_|   \__, |
                                            |___/
```

---

# Database layer

Database internally stores data as bson documents

(analogous to json objects)

---

# Bson

Limited type system

All domain concepts get converted into the bson type system

---

# Database model

> All domain concepts get converted into the bson type system

Can be helpful to define your own database model


```
REPOSITORY    <------ user ------>  DATABASE

UserBson(                           {
  _id: String,                        "_id": BsonString,
  name: String,                       "name": BsonString,
  age: Int,                           "age": BsonInteger, (32 bit)
  created: Instant,                   "created": BsonDate
  updated: Instant                    "updated": BsonDate
)                                   }
```

---

# Lots of similar model!

- POST


- GET


- database

---

# Chimney

> Lots of similar model!

Chimney helps a lot:

- reduces boilerplate


- highlights subtle transformations of data


- reduces human error

---

# Do we need it?

It's helpful, but not essential

Analyse its pro's/con's before adding it into the stack

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
