package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.Range

@TeleOp(name = "Motor Test", group = "Rover Ruckus")
@Disabled
class MotorTest : OpMode() {
    lateinit var leftMotor: DcMotor
    lateinit var rightMotor: DcMotor

    override fun init() {
        leftMotor = hardwareMap.get(DcMotor::class.java, "l_motor")
        rightMotor = hardwareMap.get(DcMotor::class.java, "r_motor")

        leftMotor.direction = DcMotorSimple.Direction.FORWARD
        rightMotor.direction = DcMotorSimple.Direction.REVERSE
    }

    override fun loop() {
        /** How much does it want to move forward - using LT */
        val throttle: Double = gamepad1.left_trigger.toDouble()
        /** How much does it want to move forward - using LT */
        val brake: Double = gamepad1.right_trigger.toDouble()

        /** How much does it want move left-right - using the right stick's X axis */
        val horizontalMovement: Double = -gamepad1.right_stick_x.toDouble()

        // Prevent overflow by clipping the values between +1 and -1
        leftMotor.power = Range.clip(throttle - brake - horizontalMovement, -1.0, 1.0)
        rightMotor.power = Range.clip(throttle - brake + horizontalMovement, -1.0, 1.0)

        telemetry.addData("Movement:", "Throttle: $throttle | Brake: $brake | Left-Right: $horizontalMovement")
        telemetry.addData("Left motor power:", leftMotor.power)
        telemetry.addData("Right motor power:", rightMotor.power)
    }
}