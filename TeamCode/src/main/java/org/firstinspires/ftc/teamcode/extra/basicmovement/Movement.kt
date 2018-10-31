package org.firstinspires.ftc.teamcode.extra.basicmovement

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.Range

@TeleOp(name = "DriveMode", group = "Movement")
class Movement : OpMode() {
    private var leftMotor: DcMotor? = null
    private var rightMotor: DcMotor? = null

    override fun init() {
        leftMotor = hardwareMap.get(DcMotor::class.java, "left_motor")
        rightMotor = hardwareMap.get(DcMotor::class.java, "right_motor")

        leftMotor?.direction = DcMotorSimple.Direction.FORWARD
        rightMotor?.direction = DcMotorSimple.Direction.REVERSE

        leftMotor?.power = 0.0
        rightMotor?.power = 0.0
    }

    override fun loop() {
        // Cu LT mergi fata
        val frontMov = gamepad1.left_trigger
        // Cu RT mergi dreapta
        val backMov = gamepad1.right_trigger

        //  Astea se duc la -1 daca dai in fata si la 1 daca dai in spate
        // de-aia pui cu - in fata
        val horizontalMov = -gamepad1.right_stick_x

        val actualLeft = Range.clip((frontMov - backMov + horizontalMov).toDouble(), -1.0, 1.0)
        val actualRight = Range.clip((frontMov - backMov - horizontalMov).toDouble(), -1.0, 1.0)

        leftMotor?.power = actualLeft
        rightMotor?.power = actualRight

        telemetry.addData("Front mov", frontMov)
        telemetry.addData("Left right mov:", horizontalMov)
    }

}