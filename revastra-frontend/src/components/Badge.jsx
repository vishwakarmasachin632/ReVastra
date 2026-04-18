import { statusTone } from '../utils/helpers';

export default function Badge({ children, tone }) {
  return <span className={`badge ${tone || statusTone(children)}`}>{children}</span>;
}
