---
author: Rohan
date: 2023-04-04
title: ScalaCheck
---

```
 ____            _        ____ _               _
/ ___|  ___ __ _| | __ _ / ___| |__   ___  ___| | __
\___ \ / __/ _` | |/ _` | |   | '_ \ / _ \/ __| |/ /
 ___) | (_| (_| | | (_| | |___| | | |  __/ (__|   <
|____/ \___\__,_|_|\__,_|\____|_| |_|\___|\___|_|\_\
```

---

# ScalaCheck

A framework for property testing in scala

---

# Recap

> What's "property testing" again?

---

# Recap

Properties are universal statements about your function that you can test

---

# Example

> String.toUpperCase

Example properties:

- the input string and output string are always the same length

```scala
s.length == s.toUpperCase.length // for all s
```

- uppercasing is idempotent

```scala
s.toUpperCase == s.toUpperCase.toUpperCase // for all s
```

---

# No outputs needed

Properties can be checked on the fly

Unlike unit tests you just need input data

---

# Generator

Generator's needed to supply random unbiased inputs

---

# Amortised testing

Each build is testing new inputs

Over time coverage is growing

---

# Agenda

`ScalaCheck` demo

- how to write tests


- minimisation/shrinking


- defining your own generators

---

```
 ____            _        ____ _               _
/ ___|  ___ __ _| | __ _ / ___| |__   ___  ___| | __
\___ \ / __/ _` | |/ _` | |   | '_ \ / _ \/ __| |/ /
 ___) | (_| (_| | | (_| | |___| | | |  __/ (__|   <
|____/ \___\__,_|_|\__,_|\____|_| |_|\___|\___|_|\_\
```

Demo time!

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

# ScalaCheck

A scala implementation of a property testing framework

---

# ScalaCheck

Provides:

- test harness to check properties


- base generators


- tools to build new generators from old ones

---

# Minimisation

ScalaCheck has tooling for generating the "smallest" input that reproduces a bug

---

# Random sampling

ScalaCheck might take a while to find some bugs

Those values will usually be things you didn't consider

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
