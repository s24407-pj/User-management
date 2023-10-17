import {Button, useColorMode} from "@chakra-ui/react";
import { MoonIcon, SunIcon } from '@chakra-ui/icons';

function ToggleTheme() {
    const { colorMode, toggleColorMode } = useColorMode()
    return (
            <Button size="lg" onClick={toggleColorMode} variant={"ghost"}>
                {colorMode === 'light' ? <MoonIcon/> : <SunIcon/>}
            </Button>
    )
}

export default ToggleTheme;