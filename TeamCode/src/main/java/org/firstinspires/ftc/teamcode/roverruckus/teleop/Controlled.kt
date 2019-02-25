package org.firstinspires.ftc.teamcode.roverruckus.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.roverruckus.utils.Motors
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot

@TeleOp(name = "Controlled", group = "Rover Ruckus")
class Controlled : OpMode() {
    val robot = Robot(this)

    val maxSpeed = 0.6
    val minSpeed = 0.3
    var currentSpeedLimit = minSpeed
    var isLiftUp: Boolean = false

    override fun init() {
        robot.init()
    }

    override fun loop() {
        if (robot.motors[Motors.Lift]?.isBusy == false || robot.motors[Motors.Lift]?.power == 0.0) {
            robot.motors[Motors.Lift]?.power = 0.0
            robot.motors[Motors.Lift]?.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            robot.motors[Motors.Lift]?.targetPosition = 0
        }

        //======================
        //=== SPEED SETTINGS ===
        //======================
        if (gamepad1.dpad_up) {
            currentSpeedLimit = Range.clip(currentSpeedLimit + 0.01, minSpeed, maxSpeed)
        }

        if (gamepad1.dpad_down) {
            currentSpeedLimit = Range.clip(currentSpeedLimit - 0.01, minSpeed, maxSpeed)
        }
        //=======================


        //================
        //=== MOVEMENT ===
        //================
        val throttle: Double = gamepad1.left_trigger.toDouble()
        val brake: Double = gamepad1.right_trigger.toDouble()
        val forwardMovement = throttle - brake
        val lateralMovement = -gamepad1.left_stick_x.toDouble()

        val movFront = Range.clip(forwardMovement - lateralMovement, -currentSpeedLimit, currentSpeedLimit)
        val movBack = Range.clip(forwardMovement + lateralMovement, -currentSpeedLimit, currentSpeedLimit)
        robot.setMotorPower(Motors.LeftFront, movBack)
        robot.setMotorPower(Motors.RightFront, movFront)
        robot.setMotorPower(Motors.LeftBack, movBack)
        robot.setMotorPower(Motors.RightBack, movFront)
        //================

        //===============
        //=== LIFTING ===
        //===============
        if (gamepad1.a && !(robot.motors[Motors.Lift]?.isBusy!!)) {
            robot.setMotorMode(Motors.Lift, DcMotor.RunMode.STOP_AND_RESET_ENCODER)
            // 28 * 3.7 * ()

            robot.motors[Motors.Lift]?.targetPosition = (28 * 3.7 * if (isLiftUp) 72 else -72 ).toInt()

            robot.motors[Motors.Lift]?.power = Robot.DEFAULT_MOTOR_POWER
            robot.motors[Motors.Lift]?.mode = DcMotor.RunMode.RUN_TO_POSITION

            isLiftUp = !isLiftUp
        }
        //===============


        telemetry.addData("LIFTING", "IsLiftUp: $isLiftUp, CurrentPos: ${robot.motors[Motors.Lift]?.currentPosition}, TargetPos: ${robot.motors[Motors.Lift]?.targetPosition}, IsBusy: ${robot.motors[Motors.Lift]?.isBusy}, Power: ${robot.motors[Motors.Lift]?.power}")
    }
}