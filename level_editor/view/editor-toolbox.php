<?php
    
function posArg() {
    echo "c=" . $_GET["c"] . "&r=" . $_GET["r"];
}

?>

<?php function direction($d) { ?>
    <td>
        <a class="field"
            onclick="api('./?action=save_<?= $d ?>&'+posArg());return false;"
            href="./?action=save_<?= $d ?>&<?php posArg(); ?>">
            <img src="./drawable/level_box_color_none.png" />
            <img src="./drawable/level_box_type_limit_<?= $d ?>.png" />
        </a>
    </td>
<?php } ?>

<div class="col card p-0 m-2">
    <div class="card-header">
        Edit selected position
    </div>
    <div class="editor card-body">
        <h6>Select type</h6>
        <table class="m-2">
            <tr>
                <td>
                    <a class="field"
                        onclick="api('./?action=save_type&type=0&'+posArg());return false;"
                        href="./?action=save_type&type=0&<?php posArg(); ?>">
                        <img src="./drawable/level_box_hole.png" />
                    </a>
                </td>
                <td>
                    <a class="field"
                        onclick="api('./?action=save_type&type=1&'+posArg());return false;"
                        href="./?action=save_type&type=1&<?php posArg(); ?>">
                        <img src="./drawable/level_box_color_none.png"/>
                        <img src="./drawable/level_box_type_flow.png"/>
                    </a>
                </td>
                <td>
                    <a class="field"
                        onclick="api('./?action=save_type&type=4&'+posArg());return false;"
                        href="./?action=save_type&type=4&<?php posArg(); ?>">
                        <img src="./drawable/level_box_color_none.png" />
                        <img src="./drawable/level_box_type_bomb.png" />
                    </a>
                </td>
                <td>
                    <a class="field"
                        onclick="api('./?action=save_type&type=2&'+posArg());return false;"
                        href="./?action=save_type&type=2&<?php posArg(); ?>">
                        <img src="./drawable/level_box_color_none.png" />
                        <img src="./drawable/level_box_type_limit_up.png" />
                    </a>
                </td>
                <td>
                    <a class="field"
                        onclick="api('./?action=save_type&type=3&'+posArg());return false;"
                        href="./?action=save_type&type=3&<?php posArg(); ?>">
                        <img src="./drawable/level_nothing.png" />
                    </a>
                </td>
            </tr>
        </table>
        <div class="toolbox-item toolbox-item-empty">
            <h6>Select target color</h6>
            <table class="m-2">
                <tr>
                    <?php foreach (COLOR_MAPPING as $a => $b) { if ($b != "") { ?>
                        <td>
                            <a class="field"
                                onclick="api('./?action=save_color&color=<?= $a ?>&'+posArg());return false;"
                                href="./?action=save_color&color=<?= $a ?>&<?php posArg(); ?>">
                                <img src="./drawable/level_box_dest_color_<?= $b ?>.png" />
                            </a>
                        </td>
                    <?php } } ?>
                </tr>
            </table>
            <h6>Select prefilled color</h6>
            <table class="m-2">
                <tr>
                    <?php foreach (COLOR_MAPPING as $a => $b) { ?>
                        <td>
                            <a class="field" 
                                onclick="api('./?action=save_preset&color=<?= $a ?>&'+posArg());return false;"
                                href="./?action=save_preset&color=<?= $a ?>&<?php posArg(); ?>">
                                <img src="./drawable/level_box_hole.png" />
                                <?php if ($b != "") { ?>
                                    <img src="./drawable/level_box_stone_<?= $b ?>.png" />
                                <?php } ?>
                            </a>
                        </td>
                    <?php } ?>
                </tr>
            </table>
        </div>
        <div class="toolbox-item toolbox-item-flow">
            <h6>Select color</h6>
            <table class="m-2">
                <tr>
                    <?php foreach (COLOR_MAPPING as $a => $b) { if ($b != "") { ?>
                        <td>
                            <a class="field"
                                onclick="api('./?action=save_color&color=<?= $a ?>&'+posArg());return false;"
                                href="./?action=save_color&color=<?= $a ?>&<?php posArg(); ?>">
                                <img src="./drawable/level_box_color_<?= $b ?>.png" />
                                <img src="./drawable/level_box_type_flow.png" />
                            </a>
                        </td>
                    <?php } } ?>
                </tr>
            </table>
        </div>
        <div class="toolbox-item toolbox-item-bomb">
            <h6>Select color</h6>
            <table class="m-2">
                <tr>
                    <?php foreach (COLOR_MAPPING as $a => $b) { if ($b != "") { ?>
                        <td>
                            <a class="field"
                                onclick="api('./?action=save_color&color=<?= $a ?>&'+posArg());return false;"
                                href="./?action=save_color&color=<?= $a ?>&<?php posArg(); ?>">
                                <img src="./drawable/level_box_color_<?= $b ?>.png" />
                                <img src="./drawable/level_box_type_bomb.png" />
                            </a>
                        </td>
                    <?php } } ?>
                </tr>
            </table>
        </div>
        <div class="toolbox-item toolbox-item-limit">
            <h6>Select color</h6>
            <table class="m-2">
                <tr>
                    <?php foreach (COLOR_MAPPING as $a => $b) { if ($b != "") { ?>
                        <td>
                            <a class="field"
                                onclick="api('./?action=save_color&color=<?= $a ?>&'+posArg());return false;"
                                href="./?action=save_color&color=<?= $a ?>&<?php posArg(); ?>">
                                <img src="./drawable/level_box_color_<?= $b ?>.png" />
                                <img src="./drawable/level_box_type_limit_up.png" />
                            </a>
                        </td>
                    <?php } } ?>
                </tr>
            </table>
            <h6>Select direction</h6>
            <table class="m-2">
                <tr>
                    <?php 
                        direction("up");
                        direction("down");
                        direction("left");
                        direction("right");
                    ?>
                </tr><tr>
                    <?php 
                        if (ROTATING) {
                            direction("rotate_up");
                            direction("rotate_down");
                            direction("rotate_left");
                            direction("rotate_right");
                        }
                    ?>
                </tr>
            </table>
        </div>

        <?php
            $displayedToolbox = "";
            if (@$_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] == TYPE_EMPTY) {
                $displayedToolbox = "empty";
            } else if (@$_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] == TYPE_LIMIT) {
                $displayedToolbox = "limit";
            } else if (@$_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] == TYPE_BOMB) {
                $displayedToolbox = "bomb";
            } else if (@$_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] == TYPE_FLOW) {
                $displayedToolbox = "flow";
            }
        ?>
        <style>
            .toolbox-item {
                display: none;
            }
            .toolbox-item.toolbox-item-<?= $displayedToolbox ?> {
                display: block;
            }
        </style>
    </div>
</div>
