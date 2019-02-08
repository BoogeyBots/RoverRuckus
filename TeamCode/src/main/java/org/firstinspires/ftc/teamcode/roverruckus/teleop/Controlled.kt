package org.firstinspires.ftc.teamcode.roverruckus.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot


@TeleOp(name = "Controlled", group = "Rover Ruckus")
class Controlled : OpMode() {
    private lateinit var robot: Robot

    private var scoopOffset = 0.0
    private var hookOffset = 0.0
    private val hookSpeed = 0.02
    private val lockSpeed = 0.01
    private var lockOffset = 0.0
    private var leftIntakeOffset = 0.0
    private var rightIntakeOffset = 0.0
    private val intakeSpeed = 0.01
    private val minSpeed = 0.3
    private val maxSpeed = 0.65
    private var currentSpeed = 0.65

    override fun init() {
        robot = Robot(this)
        robot.init()
    }

    override fun loop() {
        ///=== MOVEMENT ===

        /** How much does it want to move forward - using LT */
        val throttle: Double = gamepad1.left_trigger.toDouble()
        /** How much does it want to move forward - using LT */
        val brake: Double = gamepad1.right_trigger.toDouble()

        /** How much does it want move left-right - using the right stick's X axis */
        val horizontalMovement: Double = -gamepad1.left_stick_x.toDouble()


        // Prevent overflow by clipping the values between +1 and -1
        robot.leftMotor.power = Range.clip(throttle - brake - horizontalMovement, -currentSpeed, currentSpeed)
        robot.rightMotor.power = Range.clip(throttle - brake + horizontalMovement, -currentSpeed, currentSpeed)
        ///======================

        ///=== SPEED SETTINGS ===
        if (gamepad1.dpad_up) {
            currentSpeed = Range.clip(currentSpeed + 0.01, minSpeed, maxSpeed)
        }
        if (gamepad1.dpad_down) {
            currentSpeed = Range.clip(currentSpeed - 0.01, minSpeed, maxSpeed)
        }
        ///=======================


        ///=== LOCK ===
        lockOffset += ((if (gamepad1.y) lockSpeed else 0.0) - (if (gamepad1.a) lockSpeed else 0.0))
        lockOffset = Range.clip(lockOffset, -0.5, 0.0)

        robot.lockServo.position = Range.clip(0.5 + lockOffset, 0.0, 1.0)
        ///======================

        ///=====================
        ///=== CONTROLLER 2 ===
        ///====================

        val armMovement: Double = -gamepad2.left_stick_y.toDouble()

        robot.leftArm.power = Range.clip(armMovement, -0.6, 0.6)
        robot.rightArm.power = Range.clip(armMovement, -0.6, 0.6)

        ///=== SCOOP ===

        scoopOffset += (gamepad2.left_trigger.toDouble() - gamepad2.right_trigger.toDouble())
        scoopOffset = Range.clip(scoopOffset, -0.05, 0.5)

        ///=== HOOK ===

        hookOffset += ((if (gamepad2.left_bumper) hookSpeed else 0.0) - (if (gamepad2.right_bumper) hookSpeed else 0.0))
        hookOffset = Range.clip(hookOffset, 0.0, 0.5)

        robot.hookServo.position = Range.clip(0.5 + hookOffset, 0.5, 1.0)

        ///=== INTAKE ==

        leftIntakeOffset += (-gamepad2.right_stick_y) * intakeSpeed
        rightIntakeOffset += gamepad2.right_stick_y * intakeSpeed
        leftIntakeOffset = Range.clip(leftIntakeOffset, -0.5, 0.0)
        rightIntakeOffset = Range.clip(rightIntakeOffset, 0.0, 0.5)

        telemetry.addData("MOVEMENT:", "Throttle: $throttle | Brake: $brake | Left-Right: $horizontalMovement")
        telemetry.addData("ARM:", "Movement: $armMovement")
        telemetry.addData("Current speed", currentSpeed)
        telemetry.addData("Hook pos", robot.hookServo.position)
    }
}