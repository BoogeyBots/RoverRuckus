package org.firstinspires.ftc.teamcode.roverruckus

import android.support.annotation.RawRes
import com.qualcomm.ftccommon.SoundPlayer
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.Range
import com.sun.tools.javac.util.Context
import com.sun.tools.javac.util.Convert
import org.firstinspires.ftc.teamcode.R
import kotlin.math.min
import android.media.MediaPlayer



@TeleOp(name = "ROBOT", group = "Rover Ruckus")
class RoverRuckus : OpMode() {
    private val hardware = RoverRuckusHardware()

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

    private lateinit var mediaPlayer: MediaPlayer

    override fun init() {
        hardware.init(hardwareMap)

        mediaPlayer = MediaPlayer.create(hardwareMap.appContext, R.raw.bonjovi)
        mediaPlayer.start()
    }

    override fun loop() {
        ///=== MOVEMENT ===========

        /** How much does it want to move forward - using LT */
        val throttle: Double = gamepad1.left_trigger.toDouble()
        /** How much does it want to move forward - using LT */
        val brake: Double = gamepad1.right_trigger.toDouble()

        /** How much does it want move left-right - using the right stick's X axis */
        val horizontalMovement: Double = -gamepad1.left_stick_x.toDouble()


        // Prevent overflow by clipping the values between +1 and -1
        hardware.leftMotorPower = Range.clip(throttle - brake - horizontalMovement, -currentSpeed, currentSpeed)
        hardware.rightMotorPower = Range.clip(throttle - brake + horizontalMovement, -currentSpeed, currentSpeed)
        ///======================

        ///=== SPEED SETTINGS ===
        if (gamepad1.dpad_up) {
            currentSpeed = Range.clip(currentSpeed + 0.01, minSpeed, maxSpeed)
        }
        if (gamepad1.dpad_down) {
            currentSpeed = Range.clip(currentSpeed - 0.01, minSpeed, maxSpeed)
        }
        ///=======================


        ///=== LOCK ==============
        lockOffset += ((if (gamepad1.y) lockSpeed else 0.0) - (if (gamepad1.a) lockSpeed else 0.0))
        lockOffset = Range.clip(lockOffset, -0.5, 0.0)

        hardware.lockServoPos = Range.clip(0.5 + lockOffset, 0.0, 1.0)
        ///======================

        ///======================
        ///==== CONTROLLER 2 ====
        ///======================

        val armMovement: Double = -gamepad2.left_stick_y.toDouble()

        hardware.leftArmPower = Range.clip(armMovement, -0.6, 0.6)
        hardware.rightArmPower = Range.clip(armMovement, -0.6, 0.6)

        ///=== SCOOP ===

        scoopOffset += (gamepad2.left_trigger.toDouble() - gamepad2.right_trigger.toDouble())
        scoopOffset = Range.clip(scoopOffset, -0.05, 0.5)

        hardware.scoopServoPos = Range.clip(0.5 + scoopOffset, 0.0, 1.0)

        ///=== HOOK ===

        hookOffset += ((if (gamepad2.left_bumper) hookSpeed else 0.0) - (if (gamepad2.right_bumper) hookSpeed else 0.0))
        hookOffset = Range.clip(hookOffset, 0.0, 0.5)

        hardware.hookServoPos = Range.clip(0.5 + hookOffset, 0.5, 1.0)

        ///=== INTAKE ==

        leftIntakeOffset += (-gamepad2.right_stick_y) * intakeSpeed
        rightIntakeOffset += gamepad2.right_stick_y * intakeSpeed
        leftIntakeOffset = Range.clip(leftIntakeOffset, -0.5, 0.0)
        rightIntakeOffset = Range.clip(rightIntakeOffset, 0.0, 0.5)

        hardware.leftIntakeServoPos = Range.clip(0.5 + leftIntakeOffset, 0.0,0.38)
        hardware.rightIntakeServoPos = Range.clip(0.5 + rightIntakeOffset, 0.62,1.0)

        telemetry.addData("MOVEMENT:", "Throttle: $throttle | Brake: $brake | Left-Right: $horizontalMovement")
        telemetry.addData("ARM:", "Movement: $armMovement")
        telemetry.addData("Current speed", currentSpeed)
        telemetry.addData("Hook pos", hardware.hookServoPos)
        telemetry.addData("Hook", hardware.hookServoPos)
        telemetry.addData("Intake", "L: ${hardware.leftIntakeServoPos}; R: ${hardware.rightIntakeServoPos}")
        telemetry.addData("Scoop", hardware.scoopServoPos)
    }

    override fun stop() {
        mediaPlayer.stop()
    }
}