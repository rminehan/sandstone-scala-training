---
author: Rohan
date: 2023-04-21
title: Scala 3!
---

```
 ____            _         _____ 
/ ___|  ___ __ _| | __ _  |___ / 
\___ \ / __/ _` | |/ _` |   |_ \   !!!
 ___) | (_| (_| | | (_| |  ___) |
|____/ \___\__,_|_|\__,_| |____/ 
```

Happy Fri-yay!

---

# Yo!

I'll show you some neat stuff from scala 3

(Context: we use scala 2.13 right now)

---

# Context

Messing around with our dod scripts for DIVA-4815

They got too complex for ammonite, so I changed it to an sbt project

---

# New stuff

- top level methods


- main methods


- enums


- extension methods

---

# New stuff

- top level methods


- main methods


- enums


- extension methods

(scala 3 has other cool stuff but I haven't used it in this project)

---

# Questions?

- how easy is it to migrate a project from scala 2 to scala 3?


- what is the value in migrating?


- should we be thinking about this in diva?

---

# Let's go!

---

# Recap

- top level methods


- main methods


- extension methods


- enums

---

# Other stuff that's interesting

- opaque types


- different approach to implicits

---

# Observations

---

# Backwards compatibility

Most scala 2 will compile on scala 3

---

# Compiler improvements

- faster


- better error messages


- more sound, less edge cases

---

# Dotty

Scala 3's compiler is based on "dot calculus" which is fundamentally different to scala 2's compiler

---

# Back to our questions

- how easy is it to migrate a project from scala 2 to scala 3?


- what is the value in migrating?


- should we be thinking about this in diva?

---

# Ease of migration

> how easy is it to migrate a project from scala 2 to scala 3?

Migrating your own code seems pretty easy

---

# Ease of migration

> how easy is it to migrate a project from scala 2 to scala 3?

Migrating your own code seems pretty easy

BUT!

All your third party scala libraries must be published for scala 3

---

# Value

> what is the value in migrating?

- faster compilation


- better error messages


- cleaner, more powerful language


- improvements to libraries

---

# Diva>

> should we be thinking about this in diva?

My take:

> cost/benefit is not there yet

---

# Notes


- wait until compiler matures more


- wait until more libraries are migrated

(play, spark, cats, scalatest, akka)


- make sure your precious intellij works well with it


- deprecation of 2.13 will force us to move

---

# Internal tools

Good places to experiment as they don't need to be so "production hardened"

---

# DoD scripts

Just an internal tool devs use

No external dependencies

---

```
__   __    _ 
\ \ / /__ | |
 \ V / _ \| |
  | | (_) |_|
  |_|\___/(_)
             
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
