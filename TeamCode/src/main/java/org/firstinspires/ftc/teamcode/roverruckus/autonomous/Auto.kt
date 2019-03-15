package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import kotlinx.coroutines.*

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.roverruckus.utils.*
import org.firstinspires.ftc.teamcode.roverruckus.utils.Servos.*
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry



@Autonomous(name = "Autonomous", group = "Rover Ruckus")
class Auto : LinearOpMode() {
    val robot = Robot(this)

    override fun runOpMode() = runBlocking {
        //============
        //=== INIT ===
        //============
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        robot.telemetry = telemetry as MultipleTelemetry
        robot.init()
        robot.initVuforia()

        robot.servos[Phone]?.position = 0.35

        //======================
        //=== WAIT FOR START ===
        //======================
        waitForStartFixed()

        //===============
        //=== LANDING ===
        //===============
        runParallel {
            while (opModeIsActive()) {
                robot.writeSensorTelemetry()
                robot.writeMotorsTelemetry()
                telemetry.update()
            }
        }

        robot.servos[Phone]!!.position = 0.55

        robot.moveLifting(up = true)
        robot.moveByDistance(10.0)
        robot.strafeByDistance(-15.0, 0.75)
        robot.moveByDistance(30.0, 0.75)

        //================
        //=== SAMPLING ===
        //================
        robot.initTfod()
        val goldPos = robot.recognizeGold()

        when (goldPos) {
            GoldPos.Left -> {
                robot.strafeByDistance(-12.0, 0.75)
                robot.strafeByDistance(12.0, 0.75)
                robot.moveByDistance(-55.0, 1.0)
            }
            GoldPos.Middle -> {
                robot.strafeByDistance(-15.0, 0.75)
                robot.strafeByDistance(12.5, 0.75)
                robot.moveByDistance(-95.0, 1.0)
            }
            GoldPos.Right -> {
                robot.strafeByDistance(-15.0, 0.75)
                robot.strafeByDistance(10.0, 0.75)
                robot.moveByDistance(-145.0, 1.0)
            }
        }


        //===================
        //=== ORIENTATION ===
        //===================
        robot.rotate(38.0, 0.3)

        robot.moveByDistance(5.0, 0.5)

        runParallel {
            robot.servos[Servos.Phone]?.position = 0.45
        }
        val depotPos = robot.recognizeVuMark()

        runParallel {
            robot.servos[Servos.Phone]?.position = 0.35
        }

        when (depotPos) {
            DepotPos.Left -> {
                robot.strafeByDistance(-15.0, 0.9)

                robot.moveForSeconds(2.1, 0.9)

                wait(1.0)

                runParallel {
                    val sw = ElapsedTime()
                    robot.setMotorPower(Motors.IntakeRotation, -0.5)
                    while (sw.seconds() < 0.89 && opModeIsActive()) { }
                    robot.setMotorPower(Motors.IntakeRotation, 0.0)
                    robot.extendArm()
                }

                when (goldPos) {
                    GoldPos.Left -> {
                        robot.moveForSeconds(2.7, -1.0)
                    }
                    GoldPos.Middle -> {
                        robot.moveForSeconds(2.7, -1.0)
                    }
                    GoldPos.Right -> {
                        robot.moveForSeconds(2.7, -1.0)
                    }
                }
            }
            DepotPos.Right -> {
                robot.rotate(150.0, 0.75)

                robot.strafeByDistance(17.5, 0.95)

                robot.moveForSeconds(2.2, 1.0)

                wait(1.0)

                runParallel {
                    val sw = ElapsedTime()
                    robot.setMotorPower(Motors.IntakeRotation, -0.5)
                    while (sw.seconds() < 0.89 && opModeIsActive()) { }
                    robot.setMotorPower(Motors.IntakeRotation, 0.0)
                    robot.extendArm()
                }

                when (goldPos) {
                    GoldPos.Left -> {
                        robot.moveForSeconds(2.5, -1.0)
                    }
                    GoldPos.Middle -> {
                        robot.moveForSeconds(2.7, -1.0)
                    }
                    GoldPos.Right -> {
                        robot.moveForSeconds(2.7, -1.0)
                    }
                }
            }
        }

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
}