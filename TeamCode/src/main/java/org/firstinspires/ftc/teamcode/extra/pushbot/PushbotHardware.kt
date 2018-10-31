package org.firstinspires.ftc.teamcode.extra.pushbot

import com.qualcomm.robotcore.hardware.*

/**
 * This class holds all the objects that represent the robot's hardware (eg. motors)
 */
class PushbotHardware {
    private lateinit var leftMotor: DcMotor
    private lateinit var rightMotor: DcMotor

    private lateinit var armMotor: DcMotor

    lateinit var leftClawServo: Servo
    lateinit var rightClawServo: Servo
    lateinit var leftStopperServo: Servo
    lateinit var rightStopperServo: Servo

    lateinit var colorSensor: ColorSensor

    val CLAW_SPEED: Double = 0.02
    val ARM_POWER: Double = 0.5

    val MID_SERVO: Double = 0.5

    var leftMotorPower: Double = 0.0
        // Custom setters for caching system
        set(value) {
            // Only set the power of the motor if the last "frame's" power is
            // different from the current one
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

    /**
     *  Initialize all the robot's components
     *  @param hardwareMap The hardware map from an OpMode
     */
    fun init(hardwareMap: HardwareMap) {
        // Get the motors from the hardware map
        leftMotor = hardwareMap.get(DcMotor::class.java, "left_motor")
        rightMotor = hardwareMap.get(DcMotor::class.java, "right_motor")
        armMotor = hardwareMap.get(DcMotor::class.java, "arm_motor")
        leftClawServo = hardwareMap.get(Servo::class.java, "left_claw_servo")
        rightClawServo = hardwareMap.get(Servo::class.java, "right_claw_servo")
        leftStopperServo = hardwareMap.get(Servo::class.java, "left_stopper_servo")
        rightStopperServo = hardwareMap.get(Servo::class.java, "right_stopper_servo")
        colorSensor = hardwareMap.get(ColorSensor::class.java, "color_sensor")

        // Set initial powers for the motors
        leftMotor.power = 0.0
        rightMotor.power = 0.0
        armMotor.power = 0.0

        // The motors have opposite directions to make the wheels run in the same direction
        leftMotor.direction = DcMotorSimple.Direction.FORWARD
        rightMotor.direction = DcMotorSimple.Direction.REVERSE
        armMotor.direction = DcMotorSimple.Direction.REVERSE

        // Idk what an encoder is but Andrei said we don't use them
        leftMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        rightMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        armMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        // TODO set servo initial position
    }
}