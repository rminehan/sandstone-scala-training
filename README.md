# Scala Training

Resources related to a series of talks I gave on scala at Sandstone starting late 2021.

The audience was devs with general experience but not much scala specific experience.

# Structure

Each talk has a folder in this directory, numbered by the order the talks were done.

The resources are generally slides and code samples.

The slides are designed to be used with a cli presentation tool `lookatme`.

Demos were usually done using simple ammonite scripts or just using the repl. We used ammonite for scala 2.13 but they should generally work with 2.12. Scripts were run with `amm [SCRIPT.sc]`.

# Methodology

The content is designed to fill the gaps that a self-taught scala developer coming from a java/python background would have:

- advanced scala syntax
- deeper JVM concepts
- functional programming
- more advanced concurrency concepts
- scala libraries like play and akka streams

# The talks

Each talk went for about an hour and was recorded using Teams.

The talks are "company property". Links to the recordings are provided but won't be accessible without Sandstone authentication.

Each recording has two links, the original Teams link for when it was recorded, plus a link to
[Sharepoint](https://sandstonetechnology.sharepoint.com/teams/Products/Shared%20Documents/Forms/AllItems.aspx?csf=1&web=1&e=lkofVK&cid=0b9d3650%2D8c77%2D431e%2Da2ad%2D5ad27a871ff4&FolderCTID=0x0120005F01FAC2D6374445965824C9BF7BB010&id=%2Fteams%2FProducts%2FShared%20Documents%2FDiVA%2FVideos%2FScala%20Videos&viewid=b95ee5d9%2D9669%2D4961%2D85f6%2D107499cb06fd)
where recordings were copied to.
The Teams links will only be accessible to people who were on that teams appointment or given special access.
The Sharepoint links were made later to make the videos accessible to anyone within Sandstone.

| Code                | Date       | Title                          | General Description                                                                                       | Slides                                                               | Teams Recording (may require special permissions)                                                                                                                                                                     | Sharepoint Recording                                                                                                                                                             |
| ----                | -----      | -------------------            | ------                                                                                                    | ---------                                                            | -----------                                                                                                                                                                                                           | ---------                                                                                                                                                                        |
| SCALA-01            | 2021-11-01 | Intro to scala                 | Shows scala in the broader context of other languages and mentions tools                                  | [./01_scala/slides.md](./01_scala/slides.md)                         | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/Edj0nSY4OwxJsNcxipldEwAB_E_zWDxxIqDuiEQSj2LYYw)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211101-SCALA-01.mp4?csf=1&web=1&e=gaifC4)            |
| TYPE-SYSTEM-01      | 2021-11-03 | Scala's Type System            | Explains the general type system construct of scala with reference to Java and the JVM                    | [./02_type_system/slides.md](./02_type_system/slides.md)             | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EY3xf6VOU0lAimsVuTjumJ4BOK9DN43yvvm4H0GMx8mrtw)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211103-TYPE-SYSTEM-01.mp4?csf=1&web=1&e=MSiVfB)      |
| PATTERN-MATCHING-01 | 2021-11-05 | Basic pattern matching         | Basic introduction to pattern matching and destructuring                                                  | [./03_pattern_matching/slides1.md](./03_pattern_matching/slides1.md) | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/ESdjHAzEOxBCkSu7PUSFzpsBSyaaDb0T8IOysfVxMbWz7Q)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211105-PATTERN-MATCHING-01.mp4?csf=1&web=1&e=CRU2Ud) |
| PATTERN-MATCHING-02 | 2021-11-08 | Advanced pattern matching      | Introduces more complex concepts like alternates, sequences, extractors                                   | [./03_pattern_matching/slides2.md](./03_pattern_matching/slides2.md) | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/Edl1GXzFPFpPiQAKU8CM46YBJ2mc4hDm1KsrS2rY57JMPQ)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211108-PATTERN-MATCHING-02.mp4?csf=1&web=1&e=btqDCB) |
| FUNCTOR-MONAD-01    | 2021-11-10 | Intro to functor and monad     | Introduce the concepts and show them with List, Option and Matrix                                         | [./04_functor_monad/slides1.md](./04_functor_monad/slides1.md)       | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EaZ0LKZc_itGh3Ka-mHKvgMBXHC_wm-mmXutLippaJmP8w)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211110-FUNCTOR-MONAD-01.mp4?csf=1&web=1&e=ryCgn6)    |
| FUNCTOR-MONAD-02    | 2021-11-12 | For comprehensions             | Understand how map/flatMap code can be translated to for comprehensions and show desugaring               | [./04_functor_monad/slides2.md](./04_functor_monad/slides2.md)       | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EceUIh_4QGhBnUV_7S6Bs4IB2X6LDZkwo1F40CFNTV49pw)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211112-FUNCTOR-MONAD-02.mp4?csf=1&web=1&e=vA8eBN)    |
| FUTURE-01           | 2021-11-15 | What is a Future?              | Explains the concept of a future and synchronous vs asynchronous                                          | [./05_future/slides1.md](./05_future/slides1.md)                     | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EXDsa47_uOxKlbUTB-KkP5wBgcZR8U_WIcGynZ4Vj_HTDg)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211115-FUTURE-01.mp4?csf=1&web=1&e=7P77iV)           |
| FUTURE-02           | 2021-11-17 | Map and flatMap                | Introduces map and flatMap showing Future is a functor and monad                                          | [./05_future/slides2.md](./05_future/slides2.md)                     | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EfBSsGkMeGBOvHmYV9Q3xUkBY9oad1RlV7iKF_h0PZJ5NA)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211117-FUTURE-02.mp4?csf=1&web=1&e=Gksheh)           |
| FUTURE-03           | 2021-11-19 | More combinators               | Introduces recovery, traverse, and other useful combinators                                               | [./05_future/slides3.md](./05_future/slides3.md)                     | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/Ed64GCDQxvNHn2W_jtfOsYgBBUne3i88pJE7AeTgdMxiRw)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211119-FUTURE-03.mp4?csf=1&web=1&e=KZhi6E)           |
| FUTURE-04           | 2021-11-22 | Execution                      | Introduces the execution context concept that powers Futures                                              | [./05_future/slides4.md](./05_future/slides4.md)                     | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EfOYKEevVstCiQM6tCYjriMBHmU1U03Txz8Z_pE-vtTLvw)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211122-FUTURE-04.mp4?csf=1&web=1&e=c8h8IC)           |
| THREAD-01           | 2021-11-24 | Threads in the JVM             | Introduces VisualVM and uses it to understand the lifecycle of threads and sharing time slices            | [./06_threads/slides1.md](./06_threads/slides1.md)                   | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/ESVFcvrBtnRAuH-VQ6E56ysBwyjkuXzvAdz_aZ8fBDlJpA)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211124-THREAD-01.mp4?csf=1&web=1&e=l0gY0s)           |
| THREAD-02           | 2021-11-26 | Thread pools                   | Showed how thread pools behave differently to freely spinning up threads                                  | [./06_threads/slides2.md](./06_threads/slides2.md)                   | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EYFXjbNEoSpJo-lRhpMjipoBImyTUMtd2xzFSA0WrEbu8w)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211126-THREAD-02.mp4?csf=1&web=1&e=LtGF1S)           |
| THREAD-03           | 2021-11-29 | Fork poin pools                | Explains the fork join pool and compares with a regular thread pool                                       | [./06_threads/slides3.md](./06_threads/slides3.md)                   | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EVHd5URuuKREv7WInrp-LUQBYCw2q6XFBBzswEmBzA9raA)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211129-THREAD-03.mp4?csf=1&web=1&e=2fsKSF)           |
| THREAD-04           | 2021-12-01 | Locks                          | Explains how locks work and how they can lead to blocking                                                 | [./06_threads/slides4.md](./06_threads/slides4.md)                   | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EXheJUn4AuxIoGqdEqIJls8BHHYNpuTQmSaauraPmF8FNQ)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211201-THREAD-04.mp4?csf=1&web=1&e=f6HRxg)           |
| THREAD-05           | 2021-12-03 | Concurrency gotchas            | Goes through common concurrency gotchas found in play apps                                                | [./06_threads/slides5.md](./06_threads/slides5.md)                   | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EfcmFGj0sLxJom_IeNgxwpABLgA9fSq8EkBI7asEyNA2JQ)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211203-THREAD-05.mp4?csf=1&web=1&e=feQfCc)           |
| IMPLICITS-01        | 2021-12-06 | Implicit parameters            | Explains how scala supplies implicit parameters like execution contexts                                   | [./07_implicits/slides1.md](./07_implicits/slides1.md)               | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/Ef2MiBUgG09FuXWGa5uxgXUBxCN6d_MH8tiU-qxxaa3K6Q)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211206-IMPLICITS-01.mp4?csf=1&web=1&e=9pFXgj)        |
| IMPLICITS-02        | 2021-12-08 | Implicit classes               | Explains how implicit classes can extend existing types                                                   | [./07_implicits/slides2.md](./07_implicits/slides2.md)               | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EdfimXQbut9Ko0IMcA8dISwBXs6384WjVs_MNrOJEhZXlQ)                                                                  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211208-IMPLICITS-02.mp4?csf=1&web=1&e=tltOT7)        |
| IMPLICITS-03        | 2021-12-10 | Implicit conversions           | Explains implicit conversions and warns against them                                                      | [./07_implicits/slides3.md](./07_implicits/slides3.md)               | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EfpN2Fg1JA1Mi53qumVGhhsBT5Ah3qd6BP5CtJ1o3j3ukQ?email=simon.hong%40sandstone.com.au)                              | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211210-IMPLICITS-03.mp4?csf=1&web=1&e=yGnSsH)        |
| IMPLICITS-04        | 2021-12-14 | Implicit scope resolution      | Explains where the compiler looks when supplying implicits                                                | [./07_implicits/slides4.md](./07_implicits/slides4.md)               | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/simon_hong_sandstone_com_au/EfeugtiudsZIh_vbn06mr8kBM916e9eYZ8yYTBjfNlAFpA?email=simon.hong%40sandstone.com.au)                              | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211214-IMPLICITS-04.mp4?csf=1&web=1&e=4LuR9G)        |
| TYPE-INFERENCE-01   | 2021-12-15 | Type Inference                 | Explains the process the compiler uses to infer types and how Nothing is used                             | [./08_type_inference/slides.md](./08_type_inference/slides.md)       | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/rohan_minehan_sandstone_com_au/EZBSNfua9XlJpADzmihA6dwB67Am96WsnUJEcjPFPCK55w?email=simon.hong%40sandstone.com.au)                           | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211215-TYPE-INFERENCE-01.mp4?csf=1&web=1&e=kRbho6)   |
| OPERATORS-01        | 2021-12-17 | Operators                      | Explains how operators are just scala methods and discusses interop issues with java                      | [./09_operators/slides.md](./09_operators/slides.md)                 | [Recording](https://sandstonetechnology-my.sharepoint.com/personal/rohan_minehan_sandstone_com_au/Documents/Teams%20Meeting%20Recordings/Scala%20training%20by%20Rohan-20211217_103238-Meeting%20Recording.mp4?web=1) | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20211217-OPERATORS-01.mp4?csf=1&web=1&e=oF3ccK)        |
| AKKA-STREAMS-01     | 2022-01-17 | Intro to stream concepts       | Gives a short demo then explains the mental model of streams without going into akka streams specifically | [./10_akka_streams/slides1.md](./10_akka_streams/slides1.md)         | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/rohan_minehan_sandstone_com_au/EfJx8M9bYKBMjE2uiTKbdq0BOJZMDbK18E1v12c02ZOF0g)                                                               | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20220117-AKKA-STREAMS-01.mp4?csf=1&web=1&e=IfuUGj)     |
| AKKA-STREAMS-02     | 2022-01-20 | Topological concepts           | Introduces the "lego blocks" of akka streams (source, flow, sink) and a few combinators                   | [./10_akka_streams/slides2.md](./10_akka_streams/slides2.md)         | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/rohan_minehan_sandstone_com_au/EX9teVc7C6BJu51_VEN-Sc0BMzS9AIkwKMfX4BmJzxu1Fg)                                                               | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20220120-AKKA-STREAMS-02.mp4?csf=1&web=1&e=KmTGQd)     |
| AKKA-STREAMS-03     | 2022-01-24 | Complex topologies             | Shows how to build complex non-linear graphs using the graph dsl                                          | [./10_akka_streams/slides3.md](./10_akka_streams/slides3.md)         | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/rohan_minehan_sandstone_com_au/EepWENoQTdlNge3htr4WYD0BTV5uyUgj4y3jwpzheNpE3Q)                                                               | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20220124-AKKA-STREAMS-03.mp4?csf=1&web=1&e=aeClnn)     |
| AKKA-STREAMS-04     | 2022-01-27 | Materialisation                | Explains the second generic typer parameter in graphs often appearing as NotUsed                          | [./10_akka_streams/slides4.md](./10_akka_streams/slides4.md)         | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/rohan_minehan_sandstone_com_au/EdMwwdW9DjxOiGK2GQiJOF8B-7Mx3cLcaZU1FAWa53mW7g)                                                               | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20220127-AKKA-STREAMS-04.mp4?csf=1&web=1&e=z4lotg)     |
| AKKA-STREAMS-05     | 2022-01-30 | Async                          | Shows how mapAsync and mapAsyncUnordered can be used to work with Futures in streams                      | [./10_akka_streams/slides5.md](./10_akka_streams/slides5.md)         | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/rohan_minehan_sandstone_com_au/EWKhtiBCm2JJjUGICfBG3RABWn-X_K-uR2tUi90QKMNV_A)                                                               | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20220131-AKKA-STREAMS-05.mp4?csf=1&web=1&e=f2OcqF)     |
| AKKA-STREAMS-06-a   | 2022-02-03 | Case study (part a)            | Examines usage of akka streams within the mlservice in Sandstone's codebase                               | [./10_akka_streams/slides6.md](./10_akka_streams/slides6.md)         | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/rohan_minehan_sandstone_com_au/EbFJqp-Q_IBEongIQT2UnrMBiWnOrAr2hkn4Lu9Ip33t6A)                                                               | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20220203-AKKA-STREAMS-06a.mp4?csf=1&web=1&e=YUqLJD)    |
| AKKA-STREAMS-06-b   | 2022-02-07 | Case study (part b)            |                                                                                                           |                                                                      | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/rohan_minehan_sandstone_com_au/EchD2kmSooxEnch-xpBGx-cBzVXbZYJYfawPBxOTIL2VMw)                                                               | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20220207-AKKA-STREAMS-06b.mp4?csf=1&web=1&e=7BaOdm)    |
| AKKA-STREAMS-06-c   | 2022-02-07 | Case study (part c)            |                                                                                                           |                                                                      | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/rohan_minehan_sandstone_com_au/EVuH6APaUDdMmnHyh2tKWkcB2umzvQ5NoZKWwzVcmXcyIQ)                                                               | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20220207-AKKA-STREAMS-06c.mp4?csf=1&web=1&e=OeqyHa)    |
| KAFKA-01            | 2022-02-10 | Kafka Concepts part 1          | Goes through basic use cases for kafka and talks about the importance of idempotent consumers             | [./11_kafka/slides1.md](./11_kafka/slides1.md)                       | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/r/personal/rohan_minehan_sandstone_com_au/Documents/Recordings/Scala%20Training%202022Q1-20220210_153221-Meeting%20Recording.mp4?csf=1&web=1&e=czcebN)  | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20220210-KAFKA-01.mp4?csf=1&web=1&e=itDtwW)            |
| KAFKA-02            | 2022-02-14 | Kafka Concepts part 2          | Explains partition design and concepts like skew, then talks about error handling                         | [./11_kafka/slides2.md](./11_kafka/slides2.md)                       | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/rohan_minehan_sandstone_com_au/EV6vt6kacvxAp6U3r2xiz4UB4T8ABQU_na4-ZB11AVQ2nw)                                                               | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20220214-KAFKA-02.mp4?csf=1&web=1&e=AaVk2W)            |
| KAFKA-03            | 2022-02-17 | Kafka Sandstone Case Study     | Examines our common kafka tools and how we use them                                                       | [./11_kafka/slides3.md](./11_kafka/slides3.md)                       | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/rohan_minehan_sandstone_com_au/EXPrkAuAOfpJpmrlAfCDufoB6YDeJdhM6wROKrZvUpog-w)                                                               | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20220217-KAFKA-03.mp4?csf=1&web=1&e=czbUrr)            |
| KAFKA-04            | 2022-02-21 | Kafka Concepts part 3          | Goes into depth about error handling and the benefits of backwards compatibility                          | [./11_kafka/slides4.md](./11_kafka/slides4.md)                       | [Recording](https://sandstonetechnology-my.sharepoint.com/:v:/g/personal/rohan_minehan_sandstone_com_au/EbNBhcVVak9Dm9AnX7jwgd4B14-XoF4VHtE-YdamKl2BEA)                                                               | [Recording](https://sandstonetechnology.sharepoint.com/:v:/r/teams/Products/Shared%20Documents/DiVA/Videos/Scala%20Videos/20220221-KAFKA-04.mp4?csf=1&web=1&e=5pNYHb)            |
| LIST-01             | 2022-02-24 | The cons list                  | Explains the internal structure of the cons list and built it ourselves                                   | [./12_list/slides1.md](./12_list/slides1.md)                         | [Recording]()                                                                                                                                                                                                         | [Recording]()                                                                                                                                                                    |
| LIST-02             | 2022-02-28 | The cons list and ADT's        | Continue enhancing our cons list api and introduce ADT's                                                  | [./12_list/slides2.md](./12_list/slides2.md)                         | [Recording]()                                                                                                                                                                                                         | [Recording]()                                                                                                                                                                    |
| LIST-03             | 2022-03-03 | Time/Space Complexity          | A quick runthrough of time/space complexity in practice                                                   | [./12_list/slides3.md](./12_list/slides3.md)                         | [Recording]()                                                                                                                                                                                                         | [Recording]()                                                                                                                                                                    |
| LIST-04             | 2022-03-07 | List performance               | Understand where List performs goodly and badly                                                           | [./12_list/slides4.md](./12_list/slides4.md)                         | [Recording]()                                                                                                                                                                                                         | [Recording]()                                                                                                                                                                    |
| LIST-05             | 2022-03-10 | Functional data structures     | Understand how functional data structures can be both immutable and efficient                             | [./12_list/slides5.md](./12_list/slides5.md)                         | [Recording]()                                                                                                                                                                                                         | [Recording]()                                                                                                                                                                    |
| RECURSION-01        | 2022-03-14 | Introduction to tail recursion | Explains how tail recursion makes recursion safer                                                         | [./13_recursion/slides1.md](./13_recursion/slides1.md)               | [Recording]()                                                                                                                                                                                                         | [Recording]()                                                                                                                                                                    |
| RECURSION-02        | 2022-03-17 | Going deeper                   | More examples of tail recusion and it's connection to loops                                               | [./13_recursion/slides2.md](./13_recursion/slides2.md)               | [Recording]()                                                                                                                                                                                                         | [Recording]()                                                                                                                                                                    |
| RECURSION-03        | 2022-03-21 | Limitations of tail recursion  | Explains the limitations of tail recursion and briefly mentions trampolining as an alternative            | [./13_recursion/slides3.md](./13_recursion/slides3.md)               | [Recording]()                                                                                                                                                                                                         | [Recording]()                                                                                                                                                                    |
| TYPE-CLASSES-01     | 2022-03-24 | Introduction to folding        | Explains the foldLeft, foldRight, reduceLeft and reduceRight methods                                      | [./14_type_classes/slides1.md](./14_type_classes/slides1.md)         | [Recording]()                                                                                                                                                                                                         | [Recording]()                                                                                                                                                                    |
| TYPE-CLASSES-02     | 2022-03-28 | Monoids                        | Introduces the monoid concept as a generalisation of folding                                              | [./14_type_classes/slides2.md](./14_type_classes/slides2.md)         | [Recording]()                                                                                                                                                                                                         | [Recording]()                                                                                                                                                                    |
| TYPE-CLASSES-03     | 2022-03-31 | Type Classes                   | Introduces the concept of a type class with monoid as our example                                         | [./14_type_classes/slides2.md](./14_type_classes/slides2.md)         | [Recording]()                                                                                                                                                                                                         | [Recording]()                                                                                                                                                                    |
