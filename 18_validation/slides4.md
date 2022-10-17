---
author: Rohan
date: 2022-10-18
title: Single Responsibility Models
---

```
 ____  _             _
/ ___|(_)_ __   __ _| | ___
\___ \| | '_ \ / _` | |/ _ \
 ___) | | | | | (_| | |  __/
|____/|_|_| |_|\__, |_|\___|
               |___/
 ____                                 _ _     _ _ _ _
|  _ \ ___  ___ _ __   ___  _ __  ___(_) |__ (_) (_) |_ _   _
| |_) / _ \/ __| '_ \ / _ \| '_ \/ __| | '_ \| | | | __| | | |
|  _ <  __/\__ \ |_) | (_) | | | \__ \ | |_) | | | | |_| |_| |
|_| \_\___||___/ .__/ \___/|_| |_|___/_|_.__/|_|_|_|\__|\__, |
               |_|                                      |___/
 __  __           _      _
|  \/  | ___   __| | ___| |___
| |\/| |/ _ \ / _` |/ _ \ / __|
| |  | | (_) | (_| |  __/ \__ \
|_|  |_|\___/ \__,_|\___|_|___/

```

---

# Today

Think more deeply about how we model domain concepts in a service

---

# How?

Use a simple play demo for our example:

```json
POST /user
{
  "name": "Boban Jones",
  "age": 26,
  "email": "bobanjones@gmail.com",
  "password": "Boban4ever"
}
```

---

# Warning

During the demo I am going to use some bad hacks

(but I won't tell you when!)

Make sure to watch right to the end!

---

# Simplified

No Future's or Security or Logging Frameworks

Removes unrelated complexity from the demo

---

# Demo time!

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

# Multiple Responsibility

When models serve two purposes,

they get pulled in different directions

---

# Multiple Responsibility

> When models serve two purposes,
>
> they get pulled in different directions

This leads to a weakening of your models,

e.g. id's become optional

---

# Security

Widening your models can open potential security vulnerabilities

---

# Single Responsibility Models

Feels like over engineering at first

But it's just a reflection that these case classes have different purposes

---

# Single Responsibility Models

> Feels like over engineering at first
>
> But it's just a reflection that these case classes have different purposes

Avoids your models becoming the "lowest common denominator"

---

# Next time

Not sure...

Probably add more endpoints to the demo and a kafka consumer

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
