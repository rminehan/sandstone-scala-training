---
author: Rohan
date: 2022-10-25
title: Play Demo Continued
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

```

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

Play framework app

---

# Layers

- controller


- service


- database (dummy)

---

# Major take aways

## Single Responsibility Models

When a model serves two purposes,

it needs to be weakened to satisfy both

e.g.

- our domain model requires an id


- the json model for `POST` can't have an id

Conclusion: make the id optional

---

# Major take aways

## Shortcuts

If we're not comfortable with our tools,

it creates a temptation to resort to using simpler models

e.g. use `String` instead of `UUID` as it's easier to serialise

---

# Thinking about models

Don't let your implementation dictate your models

Ask yourself: "how would I model this?"

Then make your implementation fall into line

---

# Analogy

Models are like bones

Get them setup the right way, the muscles and skin will follow

If they're wrong, that forces the muscles and skin to be in the wrong place too

---

# Agenda

- fix last week's demo by separating models


- add strong types to the models


- create database models analogous to the controller ones

---

# To the demo!
