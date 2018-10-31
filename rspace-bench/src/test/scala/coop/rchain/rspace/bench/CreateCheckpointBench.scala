package coop.rchain.rspace.bench
import java.util.concurrent.TimeUnit

import coop.rchain.catscontrib.TaskContrib._
import coop.rchain.rspace.history.TrieCache
import monix.eval.Task
import monix.execution.Scheduler
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import monix.execution.Scheduler.Implicits.global

class CreateCheckpointBench {
  @Benchmark
  @BenchmarkMode(Array(Mode.SingleShotTime))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Threads(1)
  @Warmup(iterations = 1)
  @Measurement(iterations = 1)
  def createCheckpoint_old(bh: Blackhole, state: CreateCheckpointBenchState): Unit = {
    TrieCache.useCache = false
    bh.consume(state.runtime.space.createCheckpoint().unsafeRunSync)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.SingleShotTime))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Threads(1)
  @Warmup(iterations = 1)
  @Measurement(iterations = 1)
  def createCheckpoint_new(bh: Blackhole, state: CreateCheckpointBenchState): Unit = {
    TrieCache.useCache = true
    bh.consume(state.runtime.space.createCheckpoint().unsafeRunSync)
  }
}

@State(Scope.Benchmark)
class CreateCheckpointBenchState extends EvalBenchStateBase {
  override val rhoScriptSource: String = "/rholang/loop-with-wks.rho"

  @Setup
  override def doSetup(): Unit = {
    super.doSetup()

    val runTask = createTest(term, runtime.reducer)
    assert(runTask.unsafeRunSync.isEmpty)
    println("doSetup() finished")
  }
}