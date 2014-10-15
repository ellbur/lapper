
Lapper: Low-Performance Actor Library
=====================================

Features:
---------

 * Complete implementation.
 
 * Everything does what you expect.
 
 * No hacks, no stack manipulation, no exceptions, no magic globals.
  
 * Pure Scala.
 
Example:
--------

    import com.github.ellbur.lapper.Lapper._
    import com.github.ellbur.lapper.Locked
    
    object Test4 extends App {
      {
        case object Go
    
        val keepGoing = Locked[Boolean](true)
    
        val bob =
          actor { r =>
            println("Begin")
    
            def countingLoop(n: Int): Next = {
              if (n < 1000000) {
                r.react {
                  case Go =>
                    countingLoop(n + 1)
                }
              }
              else {
                println("Done")
                keepGoing() = false
                done
              }
            }
            countingLoop(0)
          }
    
        def spamLoop(): Unit = {
          if (keepGoing()) {
            bob ! Go
            spamLoop()
          }
        }
        spamLoop()
      }
    }

Things to be aware of
---------------------

 * That little `r =>` is to avoid magic globals. It gives you access to `react` and friends.

 * `actor` uses trampolining to avoid growing the stack. This is why, if you are not going to call `react` again,
   you need to call `done` to end the trampoline.
 
 * Every actor has its own `Thread`.
 