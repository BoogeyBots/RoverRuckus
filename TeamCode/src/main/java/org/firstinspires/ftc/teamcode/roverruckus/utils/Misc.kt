package org.firstinspires.ftc.teamcode.roverruckus.utils

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

val elapsedTime: ElapsedTime = ElapsedTime()
var jobs: MutableList<Job> = mutableListOf<Job>()


fun LinearOpMode.wait(seconds: Double) {
    elapsedTime.reset()
    while (elapsedTime.seconds() < seconds && opModeIsActive()) { }
}

fun LinearOpMode.waitForStartFixed() {
    while (!opModeIsActive() && !isStopRequested) {
        telemetry.addData("INIT OVER", "Waiting")
        telemetry.update()
    }
}

fun runParallel(foo: () -> Unit) {
    val job = GlobalScope.launch {
        foo()
    }
    job.invokeOnCompletion {
        jobs.remove(job)
    }
    jobs.add(job)
}

suspend fun finishAllJobs() {
    for (job in jobs) {
        job.join()
    }
}

fun Double.clip(min: Double, max: Double): Double {
    return Range.clip(this, min, max)
}