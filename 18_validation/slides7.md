---
author: Rohan
date: 2022-11-08
title: AmountFinderService Demo
---

```
    _                                _
   / \   _ __ ___   ___  _   _ _ __ | |_
  / _ \ | '_ ` _ \ / _ \| | | | '_ \| __|
 / ___ \| | | | | | (_) | |_| | | | | |_
/_/   \_\_| |_| |_|\___/ \__,_|_| |_|\__|

 _____ _           _
|  ___(_)_ __   __| | ___ _ __
| |_  | | '_ \ / _` |/ _ \ '__|
|  _| | | | | | (_| |  __/ |
|_|   |_|_| |_|\__,_|\___|_|

 ____                  _
/ ___|  ___ _ ____   _(_) ___ ___
\___ \ / _ \ '__\ \ / / |/ __/ _ \
 ___) |  __/ |   \ V /| | (_|  __/
|____/ \___|_|    \_/ |_|\___\___|

 ____
|  _ \  ___ _ __ ___   ___
| | | |/ _ \ '_ ` _ \ / _ \
| |_| |  __/ | | | | | (_) |
|____/ \___|_| |_| |_|\___/
```

---

# Today

Introduce strong types to AmountFinderService Demo

---

# Context

Going to use AmountFinderService for testing

Can discuss related concepts along the way

---

# Strong types

From theory to practice

---

# Recap

```
docanalyser
AmountFinderService
def firstPotentials

                  page words:    ["3", "45", "Bill", "$3", "+120.50", "-40.34", "100", "0", "House", "192.168.0.1", "123--", ".", "yo"]
                                   0    1     2       3     4          5         6      7    8        9              10      11    12

                                   /           |             |              \
                                  /            |             |               \
           group            length=1       length=2      length=3         length=4+
            by                  /              |             |                 \
          length               /               |             |                  \
                          ["3","0","."] ["45","$3","yo"]  ["100"]         ["Bill", "+120.50", "-40.34", "House", "192.168.0.1", "123--"]
                            0   7   11    1    3    12      6               2       4          5         8        9              10
                              |                |             |                   |
                              |                |             |                   |
          filter              |                |             |                   |
            +             ["3","0",   ] ["45","$3"     ]  ["100"]         [         "120.50", "-40.34"                          "123--"]
        transform           0   7         1    3            6                        4         5                                 10


                                 \             |             |                /
                                  \            |             |               /
                                   \           |             |              /

        recombine         ["3", "0", "45", "$3", "100", "120.50", "-40.34", "123--"]
                            0    7    1     3     6      4         5         10                                   Note words have to be
                                                                                                                  kept with their original 
                                                      |                                                            indexes
                                                      |

        reorder           ["3", "45", "$3", "120.50", "-40.34", "100", "0", "123--"]
                            0    1     3     4         5         6      7    10
```

---

# Very "pair"y

All text paired up with its original index

Makes filter/map logic tricky

---

# To the demo!
