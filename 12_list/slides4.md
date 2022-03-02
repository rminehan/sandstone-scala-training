---
author: Rohan
date: 2022-03-07
title: List Performance
---

```
 _     _     _
| |   (_)___| |_
| |   | / __| __|
| |___| \__ \ |_
|_____|_|___/\__|

 ____            __
|  _ \ ___ _ __ / _| ___  _ __ _ __ ___   __ _ _ __   ___ ___
| |_) / _ \ '__| |_ / _ \| '__| '_ ` _ \ / _` | '_ \ / __/ _ \
|  __/  __/ |  |  _| (_) | |  | | | | | | (_| | | | | (_|  __/
|_|   \___|_|  |_|  \___/|_|  |_| |_| |_|\__,_|_| |_|\___\___|

```

---

# Today

Discuss performance characteristics of list

---

# Homework (factorial)

Haven't forgotten about it,

will discuss when we get to tail recursion

---

# Why are we talking about this?

> discuss performance characteristics

Helps us understand what `List` is good and bad at

(will be helpful too if we look at other collections)

---

# Example

We've got a collection of names, we need to append to

```scala
val names = getNamesFromDatabase

val updatedNames = names :+ "Boban" :+ "Bobanita"
```

Order is important

---

# Example

We've got a collection of names, we need to append to

and we often need to read near the front of the collection

```scala
val names = getNamesFromDatabase

val updatedNames = names :+ "Boban" :+ "Bobanita"

println(updatedNames.head)
println(updatedNames.take(3))
```

---

# Mega rectangle

> We've got a collection of names, we need to append to
> 
> and we often need to read near the front of the collection

```
                                   What
         -------------------------------------------------
        |        |        |          |         |          |
        |        |  READ  |  REMOVE  | INSERT  | REPLACE  |
        |        |        |          |         |          |
         -------------------------------------------------
        |        |        |          |         |          |
        | FRONT  |   x    |          |         |          |
        |        |        |          |         |          |
          ------------------------------------------------
        |        |        |          |         |          |
 Where  |  BACK  |        |          |    x    |          |
        |        |        |          |         |          |
          ------------------------------------------------
        |  ANY   |        |          |         |          |
        |  WHERE |        |          |         |          |
        |        |        |          |         |          |
          ------------------------------------------------
```

Want something efficient for those use cases

---

# List?

> (Append) We've got a collection of names, we need to append to
> 
> (Read front) and we often need to read near the front of the collection

```scala
val names = List("James", "Pranali", "Yuhan", "Feroz", "Rohan", "Lanie", "Lee")
```

How fast is reading from the front?

---

# List front

```
    Code: val names = List("James", "Pranali", "Yuhan", "Feroz", "Rohan", "Lanie", "Lee")

    Linked list:            James -> Pranali -> Yuhan -> Feroz -> Rohan -> Lanie -> Lee
                            ^
                            names


    Structurally: ConsCell("James", ConsCell("Pranali", ... , Terminus)))
                  ^        head     tail     head       ...   tail
                  names
```

`names` is a reference to the outer most cons cell

How fast is it to get the "James" data?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# List front

```
    Code: val names = List("James", "Pranali", "Yuhan", "Feroz", "Rohan", "Lanie", "Lee")

    Linked list:            James -> Pranali -> Yuhan -> Feroz -> Rohan -> Lanie -> Lee
                            ^
                            names


    Structurally: ConsCell("James", ConsCell("Pranali", ... , Terminus)))
                  ^        head     tail     head       ...   tail
                  names
```

> How fast is it to get the "James" data?

O(1)

ie. really fast

Independent of how many names there are

---

# List back

```
                 James -> Pranali -> Yuhan -> Feroz -> Rohan -> Lanie -> Lee
                 ^
                 names
```

How fast is it to get the "Lee" data?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# List back

```
                 James -> Pranali -> Yuhan -> Feroz -> Rohan -> Lanie -> Lee
                 ^
                 names  -------------------------------------------------->

```

> How fast is it to get the "Lee" data?

O(n) where n is the length of the list

Have to follow the chain all the way to the end

ie. really slow

---

# Size

What is the time complexity to compute the length of the list?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Size

> What is the time complexity to compute the length of the list?

```
                 James -> Pranali -> Yuhan -> Feroz -> Rohan -> Lanie -> Lee -> Terminus
                 ^
                 names  -------------------------------------------------------->
```

O(n) too

Need to traverse the entire list counting elements until hitting the terminus

(Homework: write a method to compute the length of a list)

---

# List Quick summary

Reading the front:  O(1)

Reading the back:   O(n)

Computing the size: O(n)

---

# Back to the mega square

> (Append) We've got a collection of names, we need to append to
> 
> (Read front) and we often need to read near the front of the collection

```
                                   What
         -------------------------------------------------
        |        |        |          |         |          |
        |        |  READ  |  REMOVE  | INSERT  | REPLACE  |
        |        |        |          |         |          |
         -------------------------------------------------
        |        |        |          |         |          |
        | FRONT  |   1    |          |         |          |
        |        |  :)    |          |         |          |
          ------------------------------------------------
        |        |        |          |         |          |
 Where  |  BACK  |   n    |          |    ?    |          |
        |        |        |          |         |          |
          ------------------------------------------------
        |  ANY   |        |          |         |          |
        |  WHERE |   n    |          |         |          |
        |        |        |          |         |          |
          ------------------------------------------------
```

---

# Append?

---

# Who wants to be a one-dollar-ionairre?

For 40c:

What is the time complexity of appending to a list size n?

```scala
val names1 = ... // length n

val names2 = names1 :+ "Boban"
```

```
(A) O(1)         (B) O(n)
(C) O(logn)      (D) O(n!)
```

---

# B

For 40c:

> What is the time complexity of appending to a list size n?

```scala
val names1 = ... // length n

val names2 = names1 :+ "Boban"
```

```
(A) O(1)         (B) O(n)  <--------
(C) O(logn)      (D) O(n!)
```

---

# Appending 5

Example:

```scala
val list = List(1, 2, 3, 4)

list :+ 5

// ie. build List(1, 2, 3, 4, 5)
```

Can we reuse anything from our existing list?

---

# Appending 5?

:hmmm-parrot:

```
                                                           ------ ---     
                                                          |  5   |   |--->
                                                           ------ ---     
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

---

# Point 5 to Nil

Build from the back

```
                                                                 ------ ---     
                                                                |  5   |   |---> Nil
                                                                 ------ ---     
                                                          
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

Need to make the 4 cell point at the 5 cell

---

# Point 4 to 5?

Can't, it's immutable - need to make a new cons cell

```
                                                 ------ ---      ------ ---     
                                                |  4   |   |--->|  5   |   |---> Nil
                                                 ------ ---      ------ ---     
                                                   !=
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

---

# Point 3 to 4?

Again we need to make a new cell

```
                                 ------ ---      ------ ---      ------ ---     
                                |  3   |   |--->|  4   |   |--->|  5   |   |---> Nil
                                 ------ ---      ------ ---      ------ ---     
                                   !=              !=
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

---

# Lump in the carpet

We have to work our way back to the front of the list

Can't reuse any of the existing structure

:sad-parrot:

---

# Final result

```
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---     
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |--->|  5   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---     
   !=              !=              !=              !=
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

Had to recreate n cells

O(n) time

---

# Space complexity

```
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---     
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |--->|  5   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---     
   !=              !=              !=              !=
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

Also O(n), we created a completely new list

Couldn't reuse any existing structure

---

# Back to the mega square

> (Append) We've got a collection of names, we need to append to
> 
> (Read front) and we often need to read near the front of the collection

```
                                   What
         -------------------------------------------------
        |        |        |          |         |          |
        |        |  READ  |  REMOVE  | INSERT  | REPLACE  |
        |        |        |          |         |          |
         -------------------------------------------------
        |        |        |          |         |          |
        | FRONT  |   1    |          |         |          |
        |        |  :)    |          |         |          |
          ------------------------------------------------
        |        |        |          |         |          |
 Where  |  BACK  |   n    |          |    n    |          |
        |        |        |          |   :(    |          |
          ------------------------------------------------
        |  ANY   |        |          |         |          |
        |  WHERE |   n    |          |         |          |
        |        |        |          |         |          |
          ------------------------------------------------
```

Not a good fit

---

# Fill in the mega square

Time and Space

```
                                   What
         -------------------------------------------------
        |        |        |          |         |          |
        |        |  READ  |  REMOVE  | INSERT  | REPLACE  |
        |        |        |          |         |          |
         -------------------------------------------------
        |        |        |          |         |          |
        | FRONT  |   1    |    1     |    1    |    1     |  good
        |        |        |          |         |          |
          ------------------------------------------------
        |        |        |          |         |          |
 Where  |  BACK  |   n    |    n     |    n    |    n     |  bad
        |        |        |          |         |          |
          ------------------------------------------------
        |  ANY   |        |          |         |          |
        |  WHERE |   n    |    n     |    n    |    n     |  bad (worst case)
        |        |        |          |         |          |
          ------------------------------------------------
```

List works well at the left and badly at the right

---

# Summary

List works well at the left and badly at the right

ie. List is communist

---

# Last question

---

# Who wants to be a one-dollar-ionairre?

For 55c:

What is the time complexity of concatenating two lists length n and m?

```scala
val list1 = ...  // length n

val list2 = ...  // length m

val combined = list1 ++ list2
```

```
(A) O(n)         (B) O(m)
(C) O(n + m)     (D) O((n^2 + m^2)!!)
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# A

For 55c:

> What is the time complexity of concatenating two lists length n and m?

```scala
val list1 = ...  // length n

val list2 = ...  // length m

val combined = list1 ++ list2
```

```
(A) O(n) <----   (B) O(m)
(C) O(n + m)     (D) O((n^2 + m^2)!!)
```

The length of the left list

Completely independent of the right list

---

# Example

```scala
List(1, 2, 3) ++ List(4, 5, 6, 7, 8)
```

---

# Prepend vs Append

```scala
List(1, 2, 3) ++ List(4, 5, 6, 7, 8)
```

Two ways to think about this:

- prepend to the right list (good)


- append to the left list (bad)

---

# Start with right list

```scala
List(1, 2, 3) ++ List(4, 5, 6, 7, 8)
```

```
               4 -> 5 -> 6 -> 7 -> 8
```

---

# Prepend 3

```scala
List(1, 2, 3) ++ List(4, 5, 6, 7, 8)
//         ^ 
```

```
          3 -> 4 -> 5 -> 6 -> 7 -> 8
```

1 step

---

# Prepend 2

```scala
List(1, 2, 3) ++ List(4, 5, 6, 7, 8)
//      ^ 
```

```
     2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8
```

2 steps

---

# Prepend 1

```scala
List(1, 2, 3) ++ List(4, 5, 6, 7, 8)
//   ^ 
```

```
1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8
```

3 steps

---

# The essence

We reused the structure of the right list

Number of steps depends on the length of the left list

---

# Pro tip!

If you're concatenating a massive list with a little list,

and if order doesn't matter,

put the little one on the left

```scala
little ++ big
```

---

```
 ____                      
|  _ \ ___  ___ __ _ _ __  
| |_) / _ \/ __/ _` | '_ \ 
|  _ <  __/ (_| (_| | |_) |
|_| \_\___|\___\__,_| .__/ 
                    |_|    
```

---

# Communist list

```
                                   What
         -------------------------------------------------
        |        |        |          |         |          |
        |        |  READ  |  REMOVE  | INSERT  | REPLACE  |
        |        |        |          |         |          |
         -------------------------------------------------
        |        |        |          |         |          |
        | FRONT  |   1    |    1     |    1    |    1     |  good
        |        |        |          |         |          |
          ------------------------------------------------
        |        |        |          |         |          |
 Where  |  BACK  |   n    |    n     |    n    |    n     |  bad
        |        |        |          |         |          |
          ------------------------------------------------
        |  ANY   |        |          |         |          |
        |  WHERE |   n    |    n     |    n    |    n     |  bad (worst case)
        |        |        |          |         |          |
          ------------------------------------------------
```

---

# Other stats

Getting list size: O(n)

Concatenating lists: O(length of left list)

---

# Reuse

List works well when you can reuse structure

More on this next time

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
