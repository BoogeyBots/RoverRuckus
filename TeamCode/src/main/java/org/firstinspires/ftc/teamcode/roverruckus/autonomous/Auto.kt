package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import kotlinx.coroutines.*

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot
import org.firstinspires.ftc.teamcode.roverruckus.utils.elapsedTime
import org.firstinspires.ftc.teamcode.roverruckus.utils.wait
import org.firstinspires.ftc.teamcode.roverruckus.utils.waitForStartFixed

@Autonomous(name = "Autonomous", group = "Rover Ruckus")
class Auto : LinearOpMode() {
    val robot = Robot(this)
    var jobs: MutableList<Job> = mutableListOf<Job>()

    override fun runOpMode() = runBlocking {
        //============
        //=== INIT ===
        //============
        robot.init()
        robot.initVuforia()

        //======================
        //=== WAIT FOR START ===
        //======================
        waitForStartFixed()

        //===============
        //=== LANDING ===
        //===============
        robot.moveLifting(up = true)
        robot.moveByDistance(-10.0)

        runParallel {
            robot.moveLifting(up = false)
        }

        robot.strafeByDistance(-10.0)
        robot.moveByDistance(10.0)


        //================
        //=== SAMPLING ===
        //================
        robot.initTfod()
        var goldPos = robot.recognizeGold()

        robot.strafeByDistance(-20.0)
        robot.strafeByDistance(12.0)
        runParallel {
            elapsedTime.reset()
            while (elapsedTime.seconds() < 3.0) {
                telemetry.addData("GoldPos", goldPos.name)
            }
        }
        when (goldPos) {
            GoldPos.Left -> {
                robot.moveByDistance(-80.0)
            }
            GoldPos.Middle -> {
                robot.moveByDistance(-110.0)
            }
            GoldPos.Right -> {
                robot.moveByDistance(-160.0)
            }
        }


        //===================
        //=== ORIENTATION ===
        //===================
//        robot.strafeByDistance(-10.0)
//        robot.moveByDistance(-100.0)
//        // rotate 45 left
//        var depotPos = robot.recognizeVuMark()

        //================
        //=== CLAIMING ===
        //================

        //===============
        //=== PARKING ===
        //===============


        //==============
        //=== FINISH ===
        //==============
        finishAllJobs()
        //============
        //=== STOP ===
        //============

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
}