package org.firstinspires.ftc.teamcode.extra.pushbot

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class PushbotHardware {
    private lateinit var leftMotor: DcMotor
    private lateinit var rightMotor: DcMotor
    private lateinit var armMotor: DcMotor
    lateinit var leftClawServo: Servo
    lateinit var rightClawServo: Servo

    var leftMotorPower: Double = 0.0
        set(value) {
            if (value != field) {
                field = value
                leftMotor.power = field
            }
        }

    var rightMotorPower: Double = 0.0
        set(value) {
            if (value != field) {
                field = value
                rightMotor.power = field
            }
        }

    var armMotorPower: Double = 0.0
        set(value) {
            if (value != field) {
                field = value
                armMotor.power = field
            }
        }

    fun init(hardwareMap: HardwareMap) {
        leftMotor = hardwareMap.get(DcMotor::class.java, "left_motor")
        rightMotor = hardwareMap.get(DcMotor::class.java, "right_motor")
        armMotor = hardwareMap.get(DcMotor::class.java, "arm_motor")
        leftClawServo = hardwareMap.get(Servo::class.java, "left_claw_servo")
        rightClawServo = hardwareMap.get(Servo::class.java, "right_claw_servo")

        // Set initial powers for the motors
        leftMotor.power = 0.0
        rightMotor.power = 0.0
        armMotor.power = 0.0

        leftMotor.direction = DcMotorSimple.Direction.FORWARD
        rightMotor.direction = DcMotorSimple.Direction.REVERSE

        leftMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        rightMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        armMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        // TODO set servo initial position
    }
}