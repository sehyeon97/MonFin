import { REFRESH_SESSION_POPUP_BUTTON_CLASS_NAME, REFRESH_SESSION_POPUP_CLASS_NAME } from "@/style/classnames";
import { Container } from "../Container";
import { Button } from "../Button";

type RefreshSessionPopupProps = {
    onClick: () => void;
}

export function RefreshSessionPopup({ onClick }: RefreshSessionPopupProps) {
    return (
        <div className={REFRESH_SESSION_POPUP_CLASS_NAME}>
            <Container title="Your session is about to end...">
                <Button
                    className={REFRESH_SESSION_POPUP_BUTTON_CLASS_NAME}
                    text="Stay logged in"
                    onClick={onClick}
                />
            </Container>
        </div>
    );
}