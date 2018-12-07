package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.Range
import com.sun.tools.javac.util.Convert

@TeleOp(name = "ROBOT", group = "Rover Ruckus")
class RoverRuckus : OpMode() {
    private val hardware = RoverRuckusHardware()

    private var scoopOffset = 0.0
    private var hookOffset = 0.0
    private var hookSpeed = 0.01
    private var lockSpeed = 0.01
    private var lockOffset = 0.0
    private var leftIntakeOffset = 0.0
    private var rightIntakeOffset = 0.0
    private var intakeSpeed = 0.01

    override fun init() {
        hardware.init(hardwareMap)
    }

    override fun loop() {
        /** How much does it want to move forward - using LT */
        val throttle: Double = gamepad1.left_trigger.toDouble()
        /** How much does it want to move forward - using LT */
        val brake: Double = gamepad1.right_trigger.toDouble()

        /** How much does it want move left-right - using the right stick's X axis */
        val horizontalMovement: Double = -gamepad1.left_stick_x.toDouble()


        // Prevent overflow by clipping the values between +1 and -1
        hardware.leftMotorPower = Range.clip(throttle - brake - horizontalMovement, -0.75, 0.75)
        hardware.rightMotorPower = Range.clip(throttle - brake + horizontalMovement, -0.75, 0.75)

        ///=== LOCK ===

        lockOffset += ((if (gamepad1.y) lockSpeed else 0.0) - (if (gamepad1.a) lockSpeed else 0.0))
        lockOffset = Range.clip(lockOffset, -0.5, 0.0)

        hardware.lockServoPos = Range.clip(0.5 + lockOffset, 0.0, 1.0)

        ///======================
        ///==== CONTROLLER 2 ====
        ///======================

        val armMovement: Double = -gamepad2.left_stick_y.toDouble()

        hardware.leftArmPower = Range.clip(armMovement, -0.7, 0.7)
        hardware.rightArmPower = Range.clip(armMovement, -0.7, 0.7)

        ///=== SCOOP ===

        scoopOffset += (gamepad2.left_trigger.toDouble() - gamepad2.right_trigger.toDouble())
        scoopOffset = Range.clip(scoopOffset, -0.05, 0.5)

        hardware.scoopServoPos = Range.clip(0.5 + scoopOffset, 0.0, 1.0)

        ///=== HOOK ===

        hookOffset += ((if (gamepad2.x) hookSpeed else 0.0) - (if (gamepad2.b) hookSpeed else 0.0))
        hookOffset = Range.clip(hookOffset, 0.0, 0.5)

        hardware.hookServoPos = Range.clip(hookOffset, 0.0, 1.0)

        ///=== INTAKE ==

        leftIntakeOffset += (if (gamepad2.dpad_up) intakeSpeed else 0.0) - (if (gamepad2.dpad_down) intakeSpeed else 0.0)
        rightIntakeOffset += -(if (gamepad2.dpad_up) intakeSpeed else 0.0) + (if (gamepad2.dpad_down) intakeSpeed else 0.0)
        leftIntakeOffset = Range.clip(leftIntakeOffset, -0.5, 0.0)
        rightIntakeOffset = Range.clip(rightIntakeOffset, 0.0, 0.5)

        hardware.leftIntakeServoPos = Range.clip(0.5 + leftIntakeOffset,0.0,1.0)
        hardware.rightIntakeServoPos = Range.clip(0.5 + rightIntakeOffset, 0.0,1.0)

        telemetry.addData("MOVEMENT:", "Throttle: $throttle | Brake: $brake | Left-Right: $horizontalMovement")
        telemetry.addData("ARM:", "Movement: $armMovement")
    }
}