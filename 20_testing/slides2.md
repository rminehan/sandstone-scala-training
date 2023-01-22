---
author: Rohan
date: 2023-01-20
title: Motivating Property Testing
---

```
 __  __       _   _            _   _
|  \/  | ___ | |_(_)_   ____ _| |_(_)_ __   __ _
| |\/| |/ _ \| __| \ \ / / _` | __| | '_ \ / _` |
| |  | | (_) | |_| |\ V / (_| | |_| | | | | (_| |
|_|  |_|\___/ \__|_| \_/ \__,_|\__|_|_| |_|\__, |
                                           |___/
 ____                            _
|  _ \ _ __ ___  _ __   ___ _ __| |_ _   _
| |_) | '__/ _ \| '_ \ / _ \ '__| __| | | |
|  __/| | | (_) | |_) |  __/ |  | |_| |_| |
|_|   |_|  \___/| .__/ \___|_|   \__|\__, |
                |_|                  |___/
 _____         _   _
|_   _|__  ___| |_(_)_ __   __ _
  | |/ _ \/ __| __| | '_ \ / _` |
  | |  __/\__ \ |_| | | | | (_| |
  |_|\___||___/\__|_|_| |_|\__, |
                           |___/
```

---

# Today

Play a game where you have to find bugs in some code

---

# Today

Play a game where you have to find bugs in some code

The twist:

---

# Today

Play a game where you have to find bugs in some code

The twist:

You can't see the code, you can only execute it

---

# First up

```scala
/* fac(0) = 1
 * fac(n) = n * fac(n - 1), n > 0
 * IllegalArgumentException, n < 0
 */
def fac(n: Int): BigInt = ??? // implementation hidden
```

To the repl!

---

# Summary

Bug is a stack overflow for large inputs

```scala
/* fac(0) = 1
 * fac(n) = n * fac(n - 1), n > 0
 * IllegalArgumentException, n < 0
 */
def fac(n: Int): BigInt = n match {
  case 0 => BigInt(1)
  case _ if n > 0 => n * fac(n - 1)
  case _ => throw new IllegalArgumentException(s"Negative input $n for factorial")
}
```

---

# Next up

```scala
/* A string is considered uppercase if it doesn't contain a-z
 *
 * Examples:
 *
 * UPPER CASE == true
 *   "ABC"
 *   "A#"
 *   "#"
 *   ""
 *
 * UPPER CASE == false
 *   "a"
 *   "aA"
 */
def isUpperCase(s: String): Boolean = ???
```

---

# Summary

```scala
// BUGS:
// - ignores the last character of the string
// - explodes on empty string
// - considers non-Latin lowercase characters as lowercase (e.g. 'üA')
def isUpperCase(s: String): Boolean = {
  val leading = s.substring(0, s.length - 1)
  leading.toUpperCase == leading
}
```

---

# Next up

```scala
/* Uppercases the characters a-z, everything else left the same
 *
 * Examples:
 *
 *   "abc" -> "ABC"
 *   "a#"  -> "A#"
 *   ""    -> ""
 *   "XYZ" -> "XYZ"
 */
def toUpperCase(s: String): String = ???
```

---

# Summary

```scala
// BUG: removes 'q'
def toUpperCase(s: String): String = {
  s.filter(_ != 'q').map { c =>
    if (lowers.contains(c))
      Character.toUpperCase(c)
    else
      c
  }
}
```

---

# Needle in a haystack

Quite hard to find these bugs

Unlikely a unit test would stumble on them

---

# "Fuzzing" the functions

To the repl!

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

# Property Testing

When you can identify properties of your functions,

you can fuzz them with many values

---

# Needle in a haystack

That helps you to start picking up these random bugs

---

# Next time

Talk more formally about property testing
